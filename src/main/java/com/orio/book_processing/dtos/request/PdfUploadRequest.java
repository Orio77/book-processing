package com.orio.book_processing.dtos.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PdfUploadRequest {
    private MultipartFile file;
    @NotNull
    private List<PageRange> chapterPageRanges;

}
