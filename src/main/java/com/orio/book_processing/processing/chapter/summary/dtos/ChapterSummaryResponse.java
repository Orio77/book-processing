package com.orio.book_processing.processing.chapter.summary.dtos;

import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;

/**
 * API response representing a generated chapter summary.
 */
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
