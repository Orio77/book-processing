package com.orio.book_processing.processing.chapter.dtos;

import com.orio.book_processing.processing.chapter.models.ChapterSummary;

public record ChapterSummaryResponse(
        Long id,
        Long chapterId,
        String summaryText) {

    public static ChapterSummaryResponse from(ChapterSummary chapterSummary) {
        return new ChapterSummaryResponse(
                chapterSummary.getId(),
                chapterSummary.getChapter().getId(),
                chapterSummary.getSummaryText());
    }
}
