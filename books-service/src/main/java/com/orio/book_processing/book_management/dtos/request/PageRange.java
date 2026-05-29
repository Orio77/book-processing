package com.orio.book_processing.book_management.dtos.request;

/**
 * Inclusive page range used to map uploaded content to chapters.
 */
public record PageRange(int startPage, int endPage) {

    public PageRange {
        if (startPage > endPage) {
            throw new IllegalArgumentException("Start page cannot be greater than end page");
        }

    }
}
