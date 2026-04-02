package com.orio.book_processing.queue.services;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.events.JobCompletionEvent;
import com.orio.book_processing.queue.events.JobCreationEvent;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobWorkerService {

    private final List<JobHandler> handlers;
    private final JobRepository jobRepo;
    private final ApplicationEventPublisher eventPublisher;

    public Job createJob(JobType jobType, String payload) {
        Job job = new Job();
        job.setType(jobType);
        job.setPayload(payload);
        job.setStatus(JobStatus.PENDING);

        return jobRepo.saveAndFlush(job);
    }

    @Async
    @EventListener
    public void processNextJob(JobCreationEvent jobCreationEvent) {
        log.info("Looking for the next job...");

        Optional<Job> maybeJob = jobRepo.findById(jobCreationEvent.jobId());
        if (!maybeJob.isPresent()) {
            return;
        }

        Job job = maybeJob.get();

        log.info("Found next job of type {}.", job.getType().toString());

        handlers.stream().filter(h -> h.supports(job.getType())).findFirst().ifPresent(h -> {
            log.info("Found handler for the job {}.", h.toString());
            try {
                Long resultId = h.handle(job.getPayload());
                job.setStatus(JobStatus.COMPLETED);
                job.setResultId(resultId);
                log.info("Job completed successfully.");
            } catch (Exception e) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorText(e.getMessage());
                log.error("Error while completing a job {}", e.getMessage(), e);
            }
            Job completedJob = jobRepo.saveAndFlush(job);
            eventPublisher.publishEvent(new JobCompletionEvent(completedJob.getId()));
        });
    }
}
