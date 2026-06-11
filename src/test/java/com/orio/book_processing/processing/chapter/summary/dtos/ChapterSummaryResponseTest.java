package com.orio.book_processing.processing.chapter.summary.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;

class ChapterSummaryResponseTest {

    @Test
    void from_mapsIdsAndSummaryText() {
        Chapter chapter = new Chapter();
        chapter.setId(2L);
        ChapterSummary entity = new ChapterSummary();
        entity.setId(10L);
        entity.setChapter(chapter);
        entity.setSummaryText("mapped");

        ChapterSummaryResponse dto = ChapterSummaryResponse.from(entity);

        assertEquals(10L, dto.id());
        assertEquals(2L, dto.chapterId());
        assertEquals("mapped", dto.summaryText());
    }
}
