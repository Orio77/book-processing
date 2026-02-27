package com.orio.book_processing.book_management.services.chapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.dtos.request.PageRange;

@ExtendWith(MockitoExtension.class)
class ChapterServiceTest {

    @InjectMocks
    private ChapterService chapterService;
    private List<PageRange> chapterPageRanges;

    @BeforeEach
    void setUp() {
        chapterPageRanges = List.of(new PageRange(1, 10), new PageRange(11, 20));
    }

    static Stream<Arguments> chapterIndexProvider() {
        return Stream.of(
                Arguments.of(1, 0),
                Arguments.of(5, 0),
                Arguments.of(10, 0),
                Arguments.of(11, 1));
    }

    @ParameterizedTest
    @MethodSource("chapterIndexProvider")
    void test_getChapterIndex_validIndex(int pageIndex, int expectedChapterIndex) {
        int chapterIndex = chapterService.getChapterIndex(pageIndex, chapterPageRanges);
        assertEquals(expectedChapterIndex, chapterIndex);
    }

    @Test
    void test_getChapterIndex_invalid_index() {
        int index = 22;
        assertThrows(IllegalArgumentException.class,
                () -> chapterService.getChapterIndex(index, chapterPageRanges));
    }

}
