package com.orio.processing.queue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orio.processing.grpc.BooksClient;
import com.orio.proto.books.ClaimJobResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Polls the books-service job queue over gRPC: ClaimNextJob → handler →
 * CompleteJob. The PostgreSQL job table in books-service stays the single
 * source of truth; this service holds no queue state of its own.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobPoller {

    private static final List<String> CLAIMED_TYPES = Arrays.stream(JobType.values()).map(Enum::name).toList();

    private final BooksClient booksClient;
    private final List<JobHandler> handlers;

    @Scheduled(fixedDelayString = "${app.queue.poll-interval-ms:1000}")
    public void poll() {
        while (true) {
            Optional<ClaimJobResponse> maybeJob;
            try {
                maybeJob = booksClient.claimNextJob(CLAIMED_TYPES);
            } catch (Exception e) {
                log.warn("Claim poll failed (books-service unreachable?): {}", e.getMessage());
                return;
            }

            if (maybeJob.isEmpty()) {
                return;
            }
            execute(maybeJob.get());
        }
    }

    private void execute(ClaimJobResponse job) {
        log.info("Claimed job {} of type {}", job.getJobId(), job.getType());
        JobType jobType = JobType.valueOf(job.getType());

        Optional<JobHandler> handler = handlers.stream().filter(h -> h.supports(jobType)).findFirst();

        if (handler.isEmpty()) {
            log.error("No handler registered for job type {}", jobType);
            booksClient.completeJob(job.getJobId(), "FAILED", null, "No handler registered for type " + jobType);
            return;
        }

        try {
            Long resultId = handler.get().handle(job.getPayload());
            booksClient.completeJob(job.getJobId(), "COMPLETED", resultId, null);
            log.info("Job {} completed with resultId {}", job.getJobId(), resultId);
        } catch (Exception e) {
            log.error("Job {} failed: {}", job.getJobId(), e.getMessage(), e);
            booksClient.completeJob(job.getJobId(), "FAILED", null, e.getMessage());
        }
    }
}
