package com.orio.processing.chapter.summary.dtos;

import com.orio.processing.chapter.summary.models.ChapterSummary;

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
                chapterSummary.getChapterId(),
                chapterSummary.getSummaryText());
    }
}
