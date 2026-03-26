package com.orio.book_processing.queue;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.queue.Job.JobType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDispatcher {

    private final ObjectMapper objectMapper;
    private final JobWorkerService jobWorkerService;
    private final ApplicationEventPublisher eventPublisher;

    public Long enqueue(JobType jobType, Object payloadDTO) throws JsonProcessingException {
        log.info("Enqueing process started...");
        String json = objectMapper.writeValueAsString(payloadDTO);
        log.info("JSON mapped successfully");
        log.debug("Received JSON: {}", json.substring(0, Math.min(json.length() - 1, 1000)));

        Job job = jobWorkerService.createJob(jobType, json);

        log.info("Notifying of job creation {}...", job.getId());
        eventPublisher.publishEvent(new JobCreationEvent(job.getId()));

        return job.getId();
    }
}
