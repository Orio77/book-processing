package com.orio.book_processing.queue.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.orio.book_processing.queue.events.JobCompletionEvent;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobQueueControllerTest {

    @Mock
    private JobRepository jobRepo;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private JobQueueController controller;

    @BeforeEach
    void setUp() {
        controller = new JobQueueController(jobRepo, messagingTemplate);
    }

    @Test
    void notifyJobCompletion_broadcastsJobIdOnCompletedTopic() {
        controller.notifyJobCompletion(new JobCompletionEvent(77L));

        verify(messagingTemplate).convertAndSend("/topic/jobs/completed", 77L);
    }

    @Test
    void getJob_whenFound_returns200WithBody() {
        Job job = new Job();
        job.setId(10L);
        job.setType(JobType.CHAT);
        job.setStatus(JobStatus.PENDING);
        job.setPayload("{}");
        when(jobRepo.findById(10L)).thenReturn(Optional.of(job));

        ResponseEntity<Job> response = controller.getJob(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().getId());
        assertEquals(JobType.CHAT, response.getBody().getType());
    }

    @Test
    void getJob_whenMissing_returns404WithoutBody() {
        when(jobRepo.findById(404L)).thenReturn(Optional.empty());

        ResponseEntity<Job> response = controller.getJob(404L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getJobs_returnsRepositoryListWith200() {
        Job a = new Job();
        a.setId(1L);
        Job b = new Job();
        b.setId(2L);
        when(jobRepo.findAll()).thenReturn(List.of(a, b));

        ResponseEntity<List<Job>> response = controller.getJobs();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(a, b), response.getBody());
    }
}
