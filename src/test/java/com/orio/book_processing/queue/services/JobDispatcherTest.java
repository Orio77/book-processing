package com.orio.book_processing.queue.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.queue.events.JobCreationEvent;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class JobDispatcherTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JobWorkerService jobWorkerService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private JobDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new JobDispatcher(objectMapper, jobWorkerService, eventPublisher);
    }

    @Test
    void enqueue_serializesPayload_createsJob_publishesCreationEvent_returnsJobId() throws Exception {
        var payload = new Object() {
            @SuppressWarnings("unused")
            public String name = "x";
        };
        String json = "{\"name\":\"x\"}";
        when(objectMapper.writeValueAsString(payload)).thenReturn(json);

        Job saved = new Job();
        saved.setId(42L);
        saved.setType(JobType.PDF_UPLOAD);
        saved.setStatus(JobStatus.PENDING);
        saved.setPayload(json);
        when(jobWorkerService.createJob(JobType.PDF_UPLOAD, json)).thenReturn(saved);

        Long id = dispatcher.enqueue(JobType.PDF_UPLOAD, payload);

        assertEquals(42L, id);
        verify(objectMapper).writeValueAsString(payload);
        verify(jobWorkerService).createJob(JobType.PDF_UPLOAD, json);

        ArgumentCaptor<JobCreationEvent> eventCaptor = ArgumentCaptor.forClass(JobCreationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(42L, eventCaptor.getValue().jobId());
    }

    @Test
    void enqueue_jsonSerializationFailure_doesNotCreateJobOrPublish() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("bad") {
            private static final long serialVersionUID = 1L;
        });

        assertThrows(JsonProcessingException.class, () -> dispatcher.enqueue(JobType.CHAT, "x"));

        verify(jobWorkerService, never()).createJob(any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void enqueue_withVeryLongJson_stillCreatesJobAndPublishesEvent() throws Exception {
        String longJson = "{\"x\":\"" + "z".repeat(2500) + "\"}";
        var payload = new Object();
        when(objectMapper.writeValueAsString(payload)).thenReturn(longJson);

        Job saved = new Job();
        saved.setId(99L);
        saved.setType(JobType.IDEA_EXTRACTION);
        saved.setStatus(JobStatus.PENDING);
        saved.setPayload(longJson);
        when(jobWorkerService.createJob(JobType.IDEA_EXTRACTION, longJson)).thenReturn(saved);

        Long id = dispatcher.enqueue(JobType.IDEA_EXTRACTION, payload);

        assertEquals(99L, id);
        verify(jobWorkerService).createJob(JobType.IDEA_EXTRACTION, longJson);
        ArgumentCaptor<JobCreationEvent> eventCaptor = ArgumentCaptor.forClass(JobCreationEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(99L, eventCaptor.getValue().jobId());
    }
}
