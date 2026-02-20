package com.orio.book_processing.dtos.response;

import java.time.LocalDateTime;

import com.orio.book_processing.models.PDF;

public record PdfResponse(Long id, String title, int totalPages, LocalDateTime createdAt) {
    public static PdfResponse from(PDF pdf) {
        return new PdfResponse(pdf.getId(), pdf.getTitle(), pdf.getTotalPages(), pdf.getCreatedAt());
    }
}
                

    
