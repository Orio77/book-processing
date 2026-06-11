package com.orio.book_processing.processing.chapter.summary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.summary.services.ISummaryService;
import com.orio.book_processing.processing.chapter.summary.services.wrappers.ChapterSummaryService;

@ExtendWith(MockitoExtension.class)
class ChapterSummaryWorkflowTest {

    @Mock
    private ISummaryService summaryService;
    @Mock
    private ChapterService chapterService;
    @Mock
    private ChapterSummaryService chapterSummaryService;

    @InjectMocks
    private ChapterSummaryWorkflow workflow;

    @Test
    void generateChapterSummary_persistsSummaryAndReturnsId() throws LLMGenerationException {
        Chapter chapter = mock(Chapter.class);
        when(chapter.getText()).thenReturn("long text");
        when(chapterService.getChapterEagerly(3L)).thenReturn(chapter);
        when(summaryService.generateChapterSummary("long text")).thenReturn("summary text");

        when(chapterSummaryService.saveAndFlush(any(ChapterSummary.class))).thenAnswer(inv -> {
            ChapterSummary cs = inv.getArgument(0);
            cs.setId(44L);
            return cs;
        });

        assertEquals(44L, workflow.generateChapterSummary(3L));

        ArgumentCaptor<ChapterSummary> captor = ArgumentCaptor.forClass(ChapterSummary.class);
        verify(chapterSummaryService).saveAndFlush(captor.capture());
        ChapterSummary persisted = captor.getValue();
        assertEquals(chapter, persisted.getChapter());
        assertEquals("summary text", persisted.getSummaryText());
    }

    @Test
    void generateChapterSummary_whenSummaryFails_propagatesException() throws LLMGenerationException {
        Chapter chapter = mock(Chapter.class);
        when(chapter.getText()).thenReturn("t");
        when(chapterService.getChapterEagerly(1L)).thenReturn(chapter);
        when(summaryService.generateChapterSummary("t")).thenThrow(new LLMGenerationException("down"));

        assertThrows(LLMGenerationException.class, () -> workflow.generateChapterSummary(1L));
    }
}
