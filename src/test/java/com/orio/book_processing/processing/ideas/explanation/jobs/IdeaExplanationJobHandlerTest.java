package com.orio.book_processing.processing.ideas.explanation.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.processing.ideas.explanation.exceptions.IdeaExplanationGenerationException;
import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.book_processing.processing.ideas.explanation.services.IdeaExplanationService;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class IdeaExplanationJobHandlerTest {

    @Mock
    private IdeaExplanationService ideaExplanationService;

    private IdeaExplanationJobHandler handler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        handler = new IdeaExplanationJobHandler(objectMapper, ideaExplanationService);
    }

    @Test
    void supports_ideaExplanationOnly() {
        assertTrue(handler.supports(JobType.IDEA_EXPLANATION));
        assertFalse(handler.supports(JobType.IDEA_EXTRACTION));
    }

    @Test
    void handle_whenServiceReturnsExplanation_returnsSavedId() throws Exception {
        IdeaExplanation explanation = new IdeaExplanation();
        explanation.setId(77L);
        when(ideaExplanationService.createExplanation(eq(3L), eq("body"))).thenReturn(Optional.of(explanation));

        String payload = objectMapper.writeValueAsString(new IdeaExplanationRequest(3L, "body"));
        assertEquals(77L, handler.handle(payload));
        verify(ideaExplanationService).createExplanation(3L, "body");
    }

    @Test
    void handle_whenServiceReturnsEmpty_throwsIdeaExplanationGenerationException() throws Exception {
        when(ideaExplanationService.createExplanation(any(), any())).thenReturn(Optional.empty());

        String payload = objectMapper.writeValueAsString(new IdeaExplanationRequest(1L, "x"));
        assertThrows(IdeaExplanationGenerationException.class, () -> handler.handle(payload));
    }
}
