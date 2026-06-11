package com.orio.book_processing.processing.chapter.summary.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;

/**
 * Documents expected repository contracts used by the chapter summary flow (stubbed via Mockito).
 */
@ExtendWith(MockitoExtension.class)
class ChapterSummaryRepositoryMockTest {

    @Mock
    private ChapterSummaryRepository chapterSummaryRepository;

    @Test
    void findByChapterId_whenStubbedPresent_returnsListWrappedInOptional() {
        List<ChapterSummary> rows = List.of(new ChapterSummary());
        when(chapterSummaryRepository.findByChapterId(4L)).thenReturn(Optional.of(rows));

        assertEquals(Optional.of(rows), chapterSummaryRepository.findByChapterId(4L));
    }

    @Test
    void findByChapterId_whenStubbedAbsent_returnsEmptyOptional() {
        when(chapterSummaryRepository.findByChapterId(4L)).thenReturn(Optional.empty());

        assertTrue(chapterSummaryRepository.findByChapterId(4L).isEmpty());
    }
}
