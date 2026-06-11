package com.orio.book_processing.book_management.services.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.repositories.ChapterRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepo;

    @InjectMocks
    private ChapterService chapterService;

    private List<PageRange> chapterPageRanges;
    private PDF pdf;

    @BeforeEach
    void setUp() {
        chapterPageRanges = List.of(new PageRange(1, 10), new PageRange(11, 20));
        pdf = new PDF();
        pdf.setId(1L);
        pdf.setTitle("t");
    }

    static Stream<Arguments> chapterIndexProvider() {
        return Stream.of(
                Arguments.of(1, 0),
                Arguments.of(5, 0),
                Arguments.of(10, 0),
                Arguments.of(11, 1),
                Arguments.of(20, 1));
    }

    @ParameterizedTest
    @MethodSource("chapterIndexProvider")
    void getChapterIndex_validIndex(int pageIndex, int expectedChapterIndex) {
        assertEquals(expectedChapterIndex, chapterService.getChapterIndex(pageIndex, chapterPageRanges));
    }

    @Test
    void getChapterIndex_invalidIndex_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> chapterService.getChapterIndex(22, chapterPageRanges));
    }

    @Test
    void createChapters_mapsRangesToChapters() {
        List<Chapter> chapters = chapterService.createChapters(pdf, chapterPageRanges);
        assertEquals(2, chapters.size());
        assertSame(pdf, chapters.get(0).getPdf());
        assertEquals(1, chapters.get(0).getStartPage());
        assertEquals(10, chapters.get(0).getEndPage());
        assertEquals(11, chapters.get(1).getStartPage());
        assertEquals(20, chapters.get(1).getEndPage());
    }

    @Test
    void saveChapters_delegatesToRepository() {
        List<Chapter> chapters = List.of(new Chapter());
        chapterService.saveChapters(chapters);
        verify(chapterRepo).saveAll(chapters);
    }

    @Test
    void getChapter_delegatesToRepository() {
        Chapter ref = new Chapter();
        when(chapterRepo.getReferenceById(5L)).thenReturn(ref);
        assertSame(ref, chapterService.getChapter(5L));
    }

    @Test
    void getChapterEagerly_found_returnsChapter() {
        Chapter c = new Chapter();
        when(chapterRepo.findByIdWithSentences(3L)).thenReturn(Optional.of(c));
        assertSame(c, chapterService.getChapterEagerly(3L));
    }

    @Test
    void getChapterEagerly_missing_throwsEntityNotFound() {
        when(chapterRepo.findByIdWithSentences(3L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> chapterService.getChapterEagerly(3L));
    }

    @Test
    void getAllChapters_delegatesToRepository() {
        List<Chapter> list = List.of(new Chapter());
        when(chapterRepo.getByPdfId(9L)).thenReturn(list);
        assertSame(list, chapterService.getAllChapters(9L));
    }

    @Test
    void saveChapters_emptyList_stillInvokesSave() {
        chapterService.saveChapters(List.of());
        verify(chapterRepo).saveAll(anyList());
    }
}
