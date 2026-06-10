package com.orio.book_processing.queue.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.repositories.JobRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Finalizes a job (COMPLETED/FAILED) and notifies the owner over STOMP.
 * Replaces the former JobCompletionEvent + @EventListener pair; the future
 * gRPC CompleteJob handler will call this directly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobCompletionService {

    private final JobRepository jobRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public void complete(Long jobId, JobStatus status, Long resultId, String errorText) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("No job found for id " + jobId));

        job.setStatus(status);
        job.setResultId(resultId);
        job.setErrorText(errorText);
        job = jobRepo.saveAndFlush(job);

        log.info("Job {} finished with status {}", job.getId(), status);
        messagingTemplate.convertAndSendToUser(job.getUser().getId().toString(), "/queue/jobs/completed",
                job.getId());
    }
}
