package com.orio.book_processing.dtos.request;

public record PageRange(int startPage, int endPage) {

    public PageRange {
        if (startPage > endPage) {
            throw new IllegalArgumentException("Start page cannot be greater than end page");
        }

    }
}
