package com.orio.book_processing.queue.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.orio.book_processing.queue.events.JobCompletionEvent;
import com.orio.book_processing.queue.events.JobCreationEvent;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobWorkerServiceTest {

    @Mock
    private JobHandler pdfHandler;
    @Mock
    private JobHandler chatHandler;
    @Mock
    private JobRepository jobRepo;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private JobWorkerService workerService;

    @BeforeEach
    void setUp() {
        workerService = new JobWorkerService(List.of(pdfHandler, chatHandler), jobRepo, eventPublisher);
    }

    @Test
    void createJob_persistsPendingJobWithTypeAndPayload() {
        when(jobRepo.saveAndFlush(any(Job.class))).thenAnswer(invocation -> {
            Job j = invocation.getArgument(0);
            j.setId(7L);
            return j;
        });

        Job created = workerService.createJob(JobType.IDEA_EXTRACTION, "{\"k\":1}");

        assertEquals(7L, created.getId());
        assertEquals(JobType.IDEA_EXTRACTION, created.getType());
        assertEquals("{\"k\":1}", created.getPayload());
        assertEquals(JobStatus.PENDING, created.getStatus());

        ArgumentCaptor<Job> captor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepo).saveAndFlush(captor.capture());
        Job passed = captor.getValue();
        assertEquals(JobStatus.PENDING, passed.getStatus());
        assertEquals(JobType.IDEA_EXTRACTION, passed.getType());
        assertEquals("{\"k\":1}", passed.getPayload());
    }

    @Test
    void processNextJob_whenJobMissing_doesNotPersistOrPublishCompletion() {
        when(jobRepo.findById(99L)).thenReturn(Optional.empty());

        workerService.processNextJob(new JobCreationEvent(99L));

        verify(jobRepo, never()).saveAndFlush(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processNextJob_whenNoHandlerSupports_doesNotUpdateOrPublishCompletion() {
        Job job = new Job();
        job.setId(1L);
        job.setType(JobType.CHAPTER_SUMMARY);
        job.setPayload("{}");
        job.setStatus(JobStatus.PENDING);
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));
        when(pdfHandler.supports(JobType.CHAPTER_SUMMARY)).thenReturn(false);
        when(chatHandler.supports(JobType.CHAPTER_SUMMARY)).thenReturn(false);

        workerService.processNextJob(new JobCreationEvent(1L));

        verify(jobRepo, never()).saveAndFlush(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void processNextJob_onHandlerSuccess_marksCompleted_persistsAndPublishesCompletion() throws Exception {
        Job job = new Job();
        job.setId(2L);
        job.setType(JobType.PDF_UPLOAD);
        job.setPayload("{\"f\":1}");
        job.setStatus(JobStatus.PENDING);
        when(jobRepo.findById(2L)).thenReturn(Optional.of(job));
        when(pdfHandler.supports(JobType.PDF_UPLOAD)).thenReturn(true);
        when(pdfHandler.handle("{\"f\":1}")).thenReturn(500L);
        when(jobRepo.saveAndFlush(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        workerService.processNextJob(new JobCreationEvent(2L));

        ArgumentCaptor<Job> saveCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepo).saveAndFlush(saveCaptor.capture());
        Job saved = saveCaptor.getValue();
        assertEquals(JobStatus.COMPLETED, saved.getStatus());
        assertEquals(500L, saved.getResultId());

        ArgumentCaptor<JobCompletionEvent> done = ArgumentCaptor.forClass(JobCompletionEvent.class);
        verify(eventPublisher).publishEvent(done.capture());
        assertEquals(2L, done.getValue().jobId());
    }

    @Test
    void processNextJob_onHandlerFailure_marksFailed_stillPersistsAndPublishesCompletion() throws Exception {
        Job job = new Job();
        job.setId(3L);
        job.setType(JobType.CHAT);
        job.setPayload("[]");
        job.setStatus(JobStatus.PENDING);
        when(jobRepo.findById(3L)).thenReturn(Optional.of(job));
        when(pdfHandler.supports(JobType.CHAT)).thenReturn(false);
        when(chatHandler.supports(JobType.CHAT)).thenReturn(true);
        when(chatHandler.handle("[]")).thenThrow(new RuntimeException("boom"));
        when(jobRepo.saveAndFlush(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        workerService.processNextJob(new JobCreationEvent(3L));

        ArgumentCaptor<Job> saveCaptor = ArgumentCaptor.forClass(Job.class);
        verify(jobRepo).saveAndFlush(saveCaptor.capture());
        assertEquals(JobStatus.FAILED, saveCaptor.getValue().getStatus());

        verify(eventPublisher).publishEvent(eq(new JobCompletionEvent(3L)));
    }

    @Test
    void processNextJob_usesFirstSupportingHandlerInRegistrationOrder() throws Exception {
        Job job = new Job();
        job.setId(4L);
        job.setType(JobType.PDF_UPLOAD);
        job.setPayload("{}");
        job.setStatus(JobStatus.PENDING);
        when(jobRepo.findById(4L)).thenReturn(Optional.of(job));
        when(pdfHandler.supports(JobType.PDF_UPLOAD)).thenReturn(true);
        when(pdfHandler.handle("{}")).thenReturn(1L);
        when(jobRepo.saveAndFlush(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        workerService.processNextJob(new JobCreationEvent(4L));

        verify(pdfHandler).handle("{}");
        verify(chatHandler, never()).handle(any());
    }
}
