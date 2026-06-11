package com.orio.book_processing.processing.chapter.summary.services.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.summary.repositories.ChapterSummaryRepository;

@ExtendWith(MockitoExtension.class)
class ChapterSummaryServiceTest {

    @Mock
    private ChapterSummaryRepository chapterSummaryRepo;

    @InjectMocks
    private ChapterSummaryService service;

    @Test
    void save_delegatesToRepository() {
        ChapterSummary in = new ChapterSummary();
        service.save(in);
        verify(chapterSummaryRepo).save(in);
    }

    @Test
    void getReferenceById_returnsSummaryFromRepository() {
        ChapterSummary summary = new ChapterSummary();
        summary.setId(9L);
        when(chapterSummaryRepo.getReferenceById(9L)).thenReturn(summary);
        assertEquals(Optional.of(summary), service.getReferenceById(9L));
    }

    @Test
    void saveAndFlush_returnsSavedEntity() {
        ChapterSummary in = new ChapterSummary();
        ChapterSummary out = new ChapterSummary();
        out.setId(5L);
        when(chapterSummaryRepo.saveAndFlush(in)).thenReturn(out);
        assertEquals(5L, service.saveAndFlush(in).getId());
    }

    @Test
    void findByChapterId_whenPresent_returnsSummaries() {
        List<ChapterSummary> list = List.of(new ChapterSummary());
        when(chapterSummaryRepo.findByChapterId(1L)).thenReturn(Optional.of(list));
        assertEquals(Optional.of(list), service.findByChapterId(1L));
    }

    @Test
    void findByChapterId_whenEmpty_returnsEmpty() {
        when(chapterSummaryRepo.findByChapterId(1L)).thenReturn(Optional.empty());
        assertTrue(service.findByChapterId(1L).isEmpty());
    }

    @Test
    void deleteById_whenRepoSaysDeleted_returnsTrue() {
        when(chapterSummaryRepo.existsById(3L)).thenReturn(false);
        assertTrue(service.deleteById(3L));
        verify(chapterSummaryRepo).deleteById(3L);
    }

    @Test
    void deleteById_whenStillExists_returnsFalse() {
        when(chapterSummaryRepo.existsById(3L)).thenReturn(true);
        assertFalse(service.deleteById(3L));
    }
}
