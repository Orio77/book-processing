package com.orio.book_processing.book_management.services.sentence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.repositories.SentenceRepository;

@ExtendWith(MockitoExtension.class)
class SentenceServiceTest {

    @Mock
    private SentenceRepository sentenceRepo;

    @InjectMocks
    private SentenceService sentenceService;

    private PDF pdf;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        pdf = new PDF();
        pdf.setId(10L);
        pdf.setTitle("My Book");
        chapter = new Chapter();
        chapter.setId(20L);
        chapter.setTitle("Ch1");
    }

    @Test
    void createSentences_buildsEntitiesWithIndices() {
        List<String> strings = List.of("a", "b");
        List<Sentence> sentences = sentenceService.createSentences(strings, pdf, chapter, 3);
        assertEquals(2, sentences.size());
        assertEquals("a", sentences.get(0).getContent());
        assertEquals(0, sentences.get(0).getSentenceIndex());
        assertEquals(3, sentences.get(0).getPageNum());
        assertSame(pdf, sentences.get(0).getPdf());
        assertSame(chapter, sentences.get(0).getChapter());
        assertEquals(1, sentences.get(1).getSentenceIndex());
    }

    @Test
    void saveSentences_delegatesToRepository() {
        List<Sentence> list = List.of(new Sentence());
        sentenceService.saveSentences(list);
        verify(sentenceRepo).saveAll(list);
    }

    @Test
    void getSentencesByIds_delegatesToRepository() {
        List<Long> ids = List.of(1L, 2L);
        List<Sentence> expected = List.of(new Sentence());
        when(sentenceRepo.findAllById(ids)).thenReturn(expected);
        assertSame(expected, sentenceService.getSentencesByIds(ids));
    }

    @Test
    void getSentencesInRange_delegatesToRepository() {
        PageRange range = new PageRange(1, 5);
        List<Sentence> expected = List.of(new Sentence());
        when(sentenceRepo.getByPageNumBetweenAndPdfId(1, 5, 7L)).thenReturn(expected);
        assertSame(expected, sentenceService.getSentencesInRange(range, 7L));
    }

    @Test
    void getSentencesInRanges_filtersInMemory() {
        PageRange r1 = new PageRange(1, 1);
        PageRange r2 = new PageRange(3, 3);
        Sentence s1 = new Sentence();
        s1.setPageNum(1);
        Sentence s2 = new Sentence();
        s2.setPageNum(2);
        Sentence s3 = new Sentence();
        s3.setPageNum(3);
        when(sentenceRepo.getByPdfId(1L)).thenReturn(List.of(s1, s2, s3));
        List<List<Sentence>> groups = sentenceService.getSentencesInRanges(List.of(r1, r2), 1L);
        assertEquals(2, groups.size());
        assertEquals(1, groups.get(0).size());
        assertSame(s1, groups.get(0).get(0));
        assertEquals(1, groups.get(1).size());
        assertSame(s3, groups.get(1).get(0));
    }

    @Test
    void getSentencesByChapterId_delegatesToRepository() {
        List<Sentence> expected = List.of(new Sentence());
        when(sentenceRepo.getByChapterId(4L)).thenReturn(expected);
        assertSame(expected, sentenceService.getSentencesByChapterId(4L));
    }

    @Test
    void saveSentences_emptyList() {
        sentenceService.saveSentences(List.of());
        verify(sentenceRepo).saveAll(anyList());
    }
}
