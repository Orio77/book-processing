package com.orio.book_processing.queue.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Polls the PostgreSQL job queue and executes claimed jobs locally.
 * PDF_UPLOAD stays in books-service permanently; the LLM poller is a
 * temporary stand-in until processing-service claims those types over gRPC.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobPollingWorker {

    private static final Set<JobType> LOCAL_TYPES = Set.of(JobType.PDF_UPLOAD);

    // TEMPORARY: handled here until processing-service takes over via gRPC ClaimNextJob
    private static final Set<JobType> LLM_TYPES = Set.of(JobType.CHAPTER_SUMMARY, JobType.CHAT,
            JobType.IDEA_EXTRACTION, JobType.IDEA_EXPLANATION, JobType.IDEAS_EXPLANATION);

    private final List<JobHandler> handlers;
    private final JobClaimService jobClaimService;
    private final JobCompletionService jobCompletionService;

    @Scheduled(fixedDelayString = "${app.queue.poll-interval-ms:1000}")
    public void pollLocalJobs() {
        drainQueue(LOCAL_TYPES);
    }

    @Scheduled(fixedDelayString = "${app.queue.poll-interval-ms:1000}")
    public void pollLlmJobs() {
        drainQueue(LLM_TYPES);
    }

    private void drainQueue(Set<JobType> types) {
        while (true) {
            Optional<Job> maybeJob = jobClaimService.claimNext(types);
            if (maybeJob.isEmpty()) {
                return;
            }
            execute(maybeJob.get());
        }
    }

    private void execute(Job job) {
        Optional<JobHandler> handler = handlers.stream().filter(h -> h.supports(job.getType())).findFirst();

        if (handler.isEmpty()) {
            log.error("No handler registered for job type {}", job.getType());
            jobCompletionService.complete(job.getId(), JobStatus.FAILED, null,
                    "No handler registered for job type " + job.getType());
            return;
        }

        try {
            Long resultId = handler.get().handle(job.getPayload());
            jobCompletionService.complete(job.getId(), JobStatus.COMPLETED, resultId, null);
        } catch (Exception e) {
            log.error("Error while completing job {}: {}", job.getId(), e.getMessage(), e);
            jobCompletionService.complete(job.getId(), JobStatus.FAILED, null, e.getMessage());
        }
    }
}
