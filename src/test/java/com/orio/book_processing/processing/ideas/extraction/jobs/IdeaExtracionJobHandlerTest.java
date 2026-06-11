package com.orio.book_processing.processing.ideas.extraction.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.processing.ideas.extraction.services.IdeaExtractionManagementService;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class IdeaExtracionJobHandlerTest {

    @Mock
    private IdeaExtractionManagementService ideaExtractionManagementService;

    private IdeaExtracionJobHandler handler;

    @BeforeEach
    void setUp() {
        handler = new IdeaExtracionJobHandler(ideaExtractionManagementService, new ObjectMapper());
    }

    @Test
    void supports_ideaExtractionOnly() {
        assertTrue(handler.supports(JobType.IDEA_EXTRACTION));
        assertFalse(handler.supports(JobType.IDEA_EXPLANATION));
    }

    @Test
    void handle_parsesChapterIdAndDelegatesToManagementService() throws Exception {
        assertEquals(42L, handler.handle("42"));
        verify(ideaExtractionManagementService).extractIdeas(42L);
    }
}
