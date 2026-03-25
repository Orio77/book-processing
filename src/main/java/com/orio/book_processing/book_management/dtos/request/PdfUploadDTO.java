package com.orio.book_processing.book_management.dtos.request;

import java.util.List;

public record PdfUploadDTO(
        byte[] fileBytes,
        String originalFilename,
        String contentType,
        List<PageRange> chapterPageRanges) {
}
