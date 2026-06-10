package com.orio.book_processing.grpc;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.repositories.SentenceRepository;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobClaimService;
import com.orio.book_processing.queue.services.JobCompletionService;
import com.orio.proto.books.BooksServiceGrpc;
import com.orio.proto.books.ChapterRequest;
import com.orio.proto.books.ChapterTextResponse;
import com.orio.proto.books.ClaimJobRequest;
import com.orio.proto.books.ClaimJobResponse;
import com.orio.proto.books.CompleteJobRequest;
import com.orio.proto.books.CompleteJobResponse;
import com.orio.proto.books.SaveChatResponseRequest;
import com.orio.proto.books.SaveChatResponseResponse;
import com.orio.proto.books.Sentence;
import com.orio.proto.books.SentenceIdsRequest;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * gRPC facade of books-service: chapter/sentence reads for LLM workflows and
 * the claim/complete endpoints of the PostgreSQL job queue. Consumed by
 * processing-service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BooksGrpcService extends BooksServiceGrpc.BooksServiceImplBase {

    private final ChapterService chapterService;
    private final SentenceRepository sentenceRepo;
    private final JobClaimService jobClaimService;
    private final JobCompletionService jobCompletionService;
    private final ChatResponseService chatResponseService;

    @Override
    public void getChapterText(ChapterRequest request, StreamObserver<ChapterTextResponse> responseObserver) {
        try {
            Chapter chapter = chapterService.getChapterEagerly(request.getChapterId(), request.getUserId());
            responseObserver.onNext(ChapterTextResponse.newBuilder()
                    .setChapterId(chapter.getId())
                    .setTitle(chapter.getTitle() == null ? "" : chapter.getTitle())
                    .setText(chapter.getText())
                    .build());
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getChapterSentences(ChapterRequest request, StreamObserver<Sentence> responseObserver) {
        List<com.orio.book_processing.book_management.models.Sentence> sentences = sentenceRepo
                .getByChapterIdAndUserId(request.getChapterId(), request.getUserId());

        sentences.stream()
                .sorted(Comparator.comparingInt(com.orio.book_processing.book_management.models.Sentence::getSentenceIndex))
                .map(this::toProto)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getSentencesByIds(SentenceIdsRequest request, StreamObserver<Sentence> responseObserver) {
        sentenceRepo.findAllById(request.getSentenceIdsList()).stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(request.getUserId()))
                .map(this::toProto)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void claimNextJob(ClaimJobRequest request, StreamObserver<ClaimJobResponse> responseObserver) {
        List<JobType> types;
        try {
            types = request.getTypesList().stream().map(JobType::valueOf).toList();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Unknown job type: " + e.getMessage()).asRuntimeException());
            return;
        }

        ClaimJobResponse.Builder response = ClaimJobResponse.newBuilder().setJobAvailable(false);

        jobClaimService.claimNext(types).ifPresent((Job job) -> response
                .setJobAvailable(true)
                .setJobId(job.getId())
                .setType(job.getType().name())
                .setPayload(job.getPayload())
                .setUserId(job.getUser().getId()));

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void completeJob(CompleteJobRequest request, StreamObserver<CompleteJobResponse> responseObserver) {
        JobStatus status;
        try {
            status = JobStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription("Unknown job status: " + request.getStatus())
                            .asRuntimeException());
            return;
        }

        try {
            jobCompletionService.complete(request.getJobId(), status,
                    request.getResultId() == 0 ? null : request.getResultId(),
                    request.getErrorText().isEmpty() ? null : request.getErrorText());
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
            return;
        }

        responseObserver.onNext(CompleteJobResponse.newBuilder().setOk(true).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveChatResponse(SaveChatResponseRequest request,
            StreamObserver<SaveChatResponseResponse> responseObserver) {
        List<ChatContextSentenceDTO> context = request.getContextSentenceIdsList().stream()
                .map(id -> new ChatContextSentenceDTO(id, null))
                .toList();

        Long chatResponseId = chatResponseService.save(request.getChapterId(), request.getQuery(),
                request.getContent(), context, request.getUserId());

        responseObserver.onNext(SaveChatResponseResponse.newBuilder().setChatResponseId(chatResponseId).build());
        responseObserver.onCompleted();
    }

    private Sentence toProto(com.orio.book_processing.book_management.models.Sentence sentence) {
        return Sentence.newBuilder()
                .setId(sentence.getId())
                .setChapterId(sentence.getChapter().getId())
                .setSentenceIndex(sentence.getSentenceIndex())
                .setPageNum(sentence.getPageNum())
                .setContent(sentence.getContent())
                .build();
    }
}
