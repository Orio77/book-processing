package com.orio.book_processing.book_management.dtos.response;

import com.orio.book_processing.book_management.models.Chapter;

/**
 * API response representing chapter metadata.
 */
public record ChapterResponse(Long id, String title, int startPage, int endPage, Long pdfId) {
    public static ChapterResponse from(Chapter chapter) {
        return new ChapterResponse(
                chapter.getId(),
                chapter.getTitle(),
                chapter.getStartPage(),
                chapter.getEndPage(),
                chapter.getPdf().getId());
    }
}
