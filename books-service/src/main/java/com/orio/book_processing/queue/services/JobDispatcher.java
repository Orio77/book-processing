package com.orio.book_processing.queue.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enqueues a job by persisting it as PENDING — the PostgreSQL table is the
 * queue. PDF_UPLOAD is picked up by the local JobPollingWorker; LLM types
 * wait to be claimed (locally for now, by processing-service over gRPC later).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobDispatcher {

    private final ObjectMapper objectMapper;
    private final JobRepository jobRepo;
    private final UserRepository userRepo;

    public Long enqueue(JobType jobType, Object payloadDTO, Long userId) throws JsonProcessingException {
        log.info("Enqueing process started...");
        String json = objectMapper.writeValueAsString(payloadDTO);
        log.debug("Job payload JSON: {}", json.substring(0, Math.min(json.length() - 1, 1000)));

        Job job = new Job();
        job.setType(jobType);
        job.setPayload(json);
        job.setStatus(JobStatus.PENDING);
        job.setUser(userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for id: " + userId)));

        Job saved = jobRepo.saveAndFlush(job);
        log.info("Job {} of type {} enqueued as PENDING", saved.getId(), jobType);

        return saved.getId();
    }
}
