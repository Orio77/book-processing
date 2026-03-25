package com.orio.book_processing.queue;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.Job.JobStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobWorkerService {

    private final List<JobHandler> handlers;
    private final JobRepository jobRepo;

    public void processNextJob() {
        log.info("Looking for the next job...");

        Job job = jobRepo.findFirstByStatus(JobStatus.PENDING);
        if (job == null) {
            return;
        }

        log.info("Found next job of type {}.", job.getType().toString());

        handlers.stream().filter(h -> h.supports(job.getType())).findFirst().ifPresent(h -> {
            log.info("Found handler for the job {}.", h.toString());
            try {
                h.handle(job.getPayload());
                job.setStatus(JobStatus.COMPLETED);
                log.info("Job completed successfully.");
            } catch (Exception e) {
                job.setStatus(JobStatus.FAILED);
                log.error("Error while completing a job {}", e.getMessage(), e);
            }
            jobRepo.save(job);
        });
    }
}
