package com.orio.book_processing.processing.chapter.summary.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.processing.chapter.summary.ChapterSummaryWorkflow;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class ChapterSummaryJobHandlerTest {

    @Mock
    private ChapterSummaryWorkflow workflow;

    @InjectMocks
    private ChapterSummaryJobHandler handler;

    @Test
    void supports_chapterSummaryOnly() {
        assertTrue(handler.supports(JobType.CHAPTER_SUMMARY));
        assertFalse(handler.supports(JobType.CHAT));
    }

    @Test
    void handle_delegatesToWorkflowWithChapterId() throws Exception {
        when(workflow.generateChapterSummary(12L)).thenReturn(99L);
        assertEquals(99L, handler.handle("12"));
        verify(workflow).generateChapterSummary(12L);
    }

    @Test
    void handle_nonNumericPayload_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("not-a-number"));
    }
}
