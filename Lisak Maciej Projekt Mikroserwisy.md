# Mini-projekt — opis aplikacji

**Autor:** Maciej Lisak
**Temat:** Mikroserwisowa platforma do przetwarzania książek PDF z wykorzystaniem LLM

---

## 1. Opis aplikacji

Aplikacja umożliwia użytkownikom:

- przesyłanie plików PDF (książek) wraz z definicją zakresów stron poszczególnych rozdziałów,
- automatyczne dzielenie treści na rozdziały i zdania,
- zlecanie zadań przetwarzania wykorzystujących duży model językowy (LLM):
  - generowanie streszczeń rozdziałów,
  - ekstrakcja kluczowych idei rozdziału wraz z argumentami,
  - generowanie wyjaśnień idei (rekonstrukcja logiki rozumowania autora),
  - prowadzenie rozmów (chat) z modelem w kontekście wybranego fragmentu rozdziału.

Wszystkie zadania LLM są długotrwałe (od kilku sekund do kilku minut), dlatego model interakcji jest asynchroniczny: klient zleca zadanie i otrzymuje jego identyfikator, a o zakończeniu zadania jest powiadamiany przez WebSocket i pobiera wynik osobnym żądaniem.

Aplikacja powstaje na bazie istniejącego monolitu w Spring Boot, który zostanie rozbity na opisaną poniżej architekturę mikroserwisową.

---

## 2. Architektura

### 2.1. Mikroserwisy backendowe

1. **auth-service**
   - Rejestracja użytkowników i logowanie.
   - Wystawianie tokenów JWT (HMAC-SHA256).
   - Endpointy REST: `POST /auth/register`, `POST /auth/login`.
   - Posiada własny schemat bazy danych (`auth`) z tabelą użytkowników.

2. **books-service** (serwis główny / frontend-owy)
   - Główny punkt styku z klientem AJAX.
   - Zarządza encjami PDF, Chapter, Sentence, ChatResponse oraz kolejką zadań Job.
   - Przyjmuje pliki PDF, parsuje je przy użyciu Apache PDFBox, dzieli na zdania.
   - Zadania LLM są umieszczane w kolejce w bazie danych – mechanizm kolejki jest już zaimplementowany w bazie.
   - Wysyła powiadomienia WebSocket (STOMP) o zakończeniu zadań.
   - Endpointy REST pod `/api/pdf/**` oraz `/api/job/**`.
   - Schemat bazy: `books`.

3. **processing-service**
   - Wykonuje "ciężkie" zadania LLM: streszczenia rozdziałów, ekstrakcja idei, wyjaśnienia idei, chat w kontekście rozdziału.
   - Pobiera zadania z kolejki przez gRPC (`ClaimNextJob`) i finalizuje je przez gRPC (`CompleteJob`) — nie ma własnej kolejki ani dostępu do tabeli `job`.
   - Pobiera tekst rozdziału i zdania z books-service przez gRPC; wygenerowaną odpowiedź chatu zapisuje z powrotem w books-service (gRPC `SaveChatResponse` — encja `ChatResponse` pozostaje własnością books-service).
   - Serwuje odczyt/edycję wyników (`GET/PUT/DELETE /api/pdf/process/**`) jako OAuth2 resource server (wspólny sekret JWT).
   - Komunikuje się z LLM (Google Gemini, Gemini CLI lub mock).
   - Schemat bazy: `processing` (przechowuje wygenerowane streszczenia, idee i wyjaśnienia; encje odwołują się do danych books-service wyłącznie przez identyfikatory: `userId`, `chapterId`, `sentenceId`).

### 2.2. Infrastruktura

- **Eureka** — service discovery dla wszystkich mikroserwisów.
- **Spring Cloud Gateway** — pojedynczy punkt wejścia dla klienta; routuje żądania do właściwych serwisów na podstawie ścieżki URL.
- **PostgreSQL** — wspólna instancja bazy z osobnymi schematami per serwis. Kolejka zadań LLM jest realizowana poprzez tabele w bazie danych.
- **Docker Compose** — orkiestracja całości w środowisku deweloperskim.

### 2.3. Klient

Istniejący klient AJAX (TypeScript, framework SPA) komunikujący się wyłącznie z gatewayem przez REST/JSON oraz przez STOMP/WebSocket dla powiadomień. Klient nie jest konteneryzowany.

### 2.4. Schemat współdziałania

```
[Klient AJAX]
      │ REST + WebSocket (STOMP)
      ▼
[Spring Cloud Gateway] ◄──► [Eureka]
      │
      ├──► [auth-service]              /auth/**  (JWT)
      │
      ├──► [books-service]             /api/pdf/**, /api/job/**, /ws,
      │         │                      POST /api/pdf/process/**  (enqueue)
      │         │ zapisuje Job (PENDING) w tabeli books.job
      │         ▼
      │    [kolejka w PostgreSQL]
      │         ▲
      │         │ gRPC ClaimNextJob (FOR UPDATE SKIP LOCKED → IN_PROGRESS)
      │         │ gRPC CompleteJob  (COMPLETED/FAILED → STOMP do klienta)
      │         │ gRPC GetChapterText / GetChapterSentences / SaveChatResponse
      │         │
      └──► [processing-service]        GET/PUT/DELETE /api/pdf/process/**
                                       (odczyt/edycja wyników)
```

---

## 3. Wykorzystane technologie i techniki

### REST + AJAJ
Wszystkie publiczne endpointy są typu RESTful (JSON). Klient AJAX konsumuje je z poziomu przeglądarki przez gateway. Kluczowe grupy endpointów:

- `/auth/*` — rejestracja, logowanie (auth-service),
- `/api/pdf/*` — upload PDF, pobieranie metadanych rozdziałów i zdań (books-service),
- `/api/pdf/process/*` — zlecanie przetwarzania LLM (books-service umieszcza zadanie w kolejce w bazie danych, odpowiada zwracając jobId),
- `/api/pdf/chat` — chat w kontekście rozdziału,
- `/api/job/*` — status zadań i ich wyniki.

### Kolejka oparta o bazę danych — komunikacja asynchroniczna między serwisami
books-service zapisuje zadanie jako rekord `Job` w stanie `PENDING` w tabeli `books.job` — sama tabela jest kolejką (bez RabbitMQ, bez eventów Spring). processing-service cyklicznie (poller `@Scheduled`) pobiera zadania przez gRPC `ClaimNextJob`: books-service atomowo wybiera najstarsze `PENDING` zapytaniem

```sql
SELECT * FROM job
WHERE status = 'PENDING' AND type IN (:types)
ORDER BY created_at
LIMIT 1
FOR UPDATE SKIP LOCKED
```

i przestawia je na `IN_PROGRESS`, dzięki czemu zadanie trafia do dokładnie jednego workera. Po przetworzeniu processing-service wywołuje gRPC `CompleteJob` (status `COMPLETED`/`FAILED`, `resultId`, `errorText`), a books-service wysyła powiadomienie WebSocket do klienta. Zadania `PDF_UPLOAD` obsługuje lokalny worker w books-service na tej samej kolejce.

### gRPC — komunikacja synchroniczna między serwisami
processing-service korzysta z gRPC do books-service zarówno dla danych rozdziałów, jak i obsługi kolejki:

```proto
service BooksService {
  // Dane rozdziału (wejście dla LLM)
  rpc GetChapterText(ChapterRequest) returns (ChapterTextResponse);
  rpc GetChapterSentences(ChapterRequest) returns (stream Sentence);
  rpc GetSentencesByIds(SentenceIdsRequest) returns (stream Sentence);

  // Kolejka zadań (PostgreSQL w books-service)
  rpc ClaimNextJob(ClaimJobRequest) returns (ClaimJobResponse);
  rpc CompleteJob(CompleteJobRequest) returns (CompleteJobResponse);

  // Zapis wygenerowanej odpowiedzi chatu (encja należy do books-service)
  rpc SaveChatResponse(SaveChatResponseRequest) returns (SaveChatResponseResponse);
}
```

Definicja `.proto` jest dzielona poprzez wspólny moduł Maven (`shared-proto`); serwer gRPC działa w books-service na porcie 9090.

### HATEOAS / HAL
Wybrane endpointy zwracają reprezentacje z linkami HAL. Najlepiej pasuje to do encji **Job**, której stan determinuje dostępne akcje:

- Job o statusie `PENDING` → linki: `self`, `cancel`,
- Job `COMPLETED` → linki: `self`, `result` (link do wygenerowanego streszczenia / kolekcji idei / odpowiedzi chatu, w zależności od typu zadania),
- Job `FAILED` → linki: `self`, `retry`, w ciele odpowiedzi opis błędu.

Również encje **Chapter** zwracają linki warunkowe: `summary` jeśli streszczenie istnieje, `generate-summary` jeśli nie; analogicznie `ideas` / `extract-ideas`.

### Konteneryzacja i Spring Cloud
Każdy serwis dostarczany jako obraz Docker (multi-stage build, oparty na `eclipse-temurin:21-jre`). Cała aplikacja uruchamiana jednym poleceniem `docker-compose up`. Eureka pełni rolę service registry, Spring Cloud Gateway — bramy z routingiem opartym na rejestrze.

### Współdziałanie asynchroniczne
- Klient inicjuje długotrwałą operację (upload PDF, generowanie streszczenia, chat z LLM) jednym żądaniem REST i otrzymuje natychmiast `202 Accepted` z identyfikatorem zadania.
- books-service zapisuje rekord `Job` w stanie `PENDING` w kolejce w bazie danych.
- processing-service pobiera zadanie z bazy, wykonuje obliczenia (kilka sekund do kilku minut), aktualizuje wynik w bazie.
- books-service aktualizuje rekord `Job` i wysyła powiadomienie STOMP do użytkownika (`/user/queue/jobs/completed`).
- Klient po otrzymaniu powiadomienia pobiera wynik osobnym żądaniem REST (link `result` z reprezentacji HAL).

---

## 4. Podsumowanie wykorzystania wymaganych technik

| Wymaganie | Realizacja |
|---|---|
| REST (serwis główny) | Wszystkie publiczne endpointy w books-service i auth-service |
| Klient AJAX (AJAJ) | Istniejący klient SPA komunikujący się z gatewayem |
| Kontenery + Spring Cloud | Eureka + Gateway + 3 mikroserwisy w Docker Compose |
| Co najmniej 2 z {gRPC, W3C WS, RabbitMQ} | gRPC (pobieranie tekstu rozdziału), WebSocket (powiadomienia); kolejka zadań przez bazę danych |
| Współdziałanie asynchroniczne | Pełen cykl: REST → zadanie zapisywane w kolejce w bazie → przetwarzanie → powiadomienie WebSocket |
| HATEOAS / HAL | Encje Job i Chapter zwracają linki zależne od stanu |

---

## 5. Status prac

Refaktoryzacja zakończona. Monolit został rozbity na 5 serwisów (eureka-server, api-gateway, auth-service, books-service, processing-service) w wielomodułowym monorepo Maven. Eventy Spring zostały zastąpione kolejką w PostgreSQL (statusy `PENDING → IN_PROGRESS → COMPLETED/FAILED/CANCELLED`, claim przez `FOR UPDATE SKIP LOCKED`), komunikacja między serwisami odbywa się przez gRPC (kontrakt w module `shared-proto`), encje processing-service odwołują się do danych books-service przez identyfikatory, a reprezentacje `Job` i `Chapter` zawierają warunkowe linki HAL. Każdy serwis ma wielostopniowy (multi-stage) Dockerfile, a całość — wraz z PostgreSQL (schematy `auth`, `books`, `processing`), Eureką i gatewayem — uruchamia się jednym poleceniem `docker-compose up` (healthchecki + `depends_on` pilnują kolejności startu).
