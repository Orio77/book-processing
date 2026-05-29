# Information Processing Set Up

It is a web app that allows to upload pdfs, particularly books, and work with them with precision to the level of a sentence.

## Multi-module layout

| Module | Role |
|--------|------|
| `books-service` | Current monolith (PDF, chapters, jobs, auth, processing) — runnable |
| `auth-service` | Placeholder — auth extraction in a later step |
| `processing-service` | Placeholder — LLM extraction in a later step |
| `eureka-server` | Placeholder — service discovery in a later step |
| `api-gateway` | Placeholder — routing in a later step |
| `shared-common` | Shared types (skeleton) |
| `shared-proto` | gRPC contracts (skeleton) |

## Build and run

From the **repository root** (`book-processing/`):

```bash
mvn clean package                              # build all modules
mvn test                                       # tests (books-service needs PostgreSQL)

# Run books-service (builds shared-common first via -am):
mvn -pl books-service -am spring-boot:run

# Or, after mvn install:
cd books-service && mvn spring-boot:run
```

`books-service` depends on `shared-common`. Run **`mvn install`** once from the repo root (or use **`-am`**), so `shared-common` is in the local Maven repo. The parent POM skips `spring-boot:run` so the goal runs only on `books-service`, not on the aggregator.

PostgreSQL must be running with the settings from `books-service/src/main/resources/application.properties` (or your local override).
