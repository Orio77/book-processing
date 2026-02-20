package com.orio.book_processing.dtos.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class PdfUploadRequest {
    private MultipartFile file;
    @NotNull
    private List<PageRange> chapterPageRanges;

}
