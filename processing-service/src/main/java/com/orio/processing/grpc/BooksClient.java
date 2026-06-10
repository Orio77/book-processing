package com.orio.processing.grpc;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.orio.proto.books.BooksServiceGrpc;
import com.orio.proto.books.ChapterRequest;
import com.orio.proto.books.ChapterTextResponse;
import com.orio.proto.books.ClaimJobRequest;
import com.orio.proto.books.ClaimJobResponse;
import com.orio.proto.books.CompleteJobRequest;
import com.orio.proto.books.SaveChatResponseRequest;
import com.orio.proto.books.Sentence;
import com.orio.proto.books.SentenceIdsRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Typed facade over the books-service gRPC API: chapter/sentence reads, job
 * queue claim/complete, and chat response persistence.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BooksClient {

    private final BooksServiceGrpc.BooksServiceBlockingStub booksStub;

    public ChapterTextResponse getChapterText(Long chapterId, Long userId) {
        return booksStub.getChapterText(
                ChapterRequest.newBuilder().setChapterId(chapterId).setUserId(userId).build());
    }

    public List<Sentence> getChapterSentences(Long chapterId, Long userId) {
        Iterator<Sentence> iterator = booksStub.getChapterSentences(
                ChapterRequest.newBuilder().setChapterId(chapterId).setUserId(userId).build());
        return toList(iterator);
    }

    public List<Sentence> getSentencesByIds(List<Long> sentenceIds, Long userId) {
        Iterator<Sentence> iterator = booksStub.getSentencesByIds(
                SentenceIdsRequest.newBuilder().addAllSentenceIds(sentenceIds).setUserId(userId).build());
        return toList(iterator);
    }

    public Optional<ClaimJobResponse> claimNextJob(List<String> types) {
        ClaimJobResponse response = booksStub.claimNextJob(
                ClaimJobRequest.newBuilder().addAllTypes(types).build());
        return response.getJobAvailable() ? Optional.of(response) : Optional.empty();
    }

    public void completeJob(Long jobId, String status, Long resultId, String errorText) {
        booksStub.completeJob(CompleteJobRequest.newBuilder()
                .setJobId(jobId)
                .setStatus(status)
                .setResultId(resultId == null ? 0 : resultId)
                .setErrorText(errorText == null ? "" : errorText)
                .build());
    }

    public Long saveChatResponse(Long chapterId, Long userId, String query, String content,
            List<Long> contextSentenceIds) {
        return booksStub.saveChatResponse(SaveChatResponseRequest.newBuilder()
                .setChapterId(chapterId)
                .setUserId(userId)
                .setQuery(query == null ? "" : query)
                .setContent(content)
                .addAllContextSentenceIds(contextSentenceIds)
                .build()).getChatResponseId();
    }

    private List<Sentence> toList(Iterator<Sentence> iterator) {
        Iterable<Sentence> iterable = () -> iterator;
        Stream<Sentence> stream = StreamSupport.stream(iterable.spliterator(), false);
        return stream.toList();
    }
}
