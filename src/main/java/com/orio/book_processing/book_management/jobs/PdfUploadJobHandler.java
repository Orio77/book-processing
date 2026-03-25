package com.orio.book_processing.book_management.jobs;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.book_management.dtos.request.ByteArrayMultipartFile;
import com.orio.book_processing.book_management.dtos.request.PdfUploadDTO;
import com.orio.book_processing.book_management.exceptions.FileContentException;
import com.orio.book_processing.book_management.exceptions.PDFLoadingException;
import com.orio.book_processing.book_management.services.upload.PdfUploadService;
import com.orio.book_processing.queue.Job.JobType;
import com.orio.book_processing.queue.JobHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PdfUploadJobHandler implements JobHandler {

    private final PdfUploadService pdfUploadService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.PDF_UPLOAD;
    }

    @Override
    public Long handle(String payload) throws PDFLoadingException, FileContentException, JsonProcessingException {
        PdfUploadDTO upload = objectMapper.readValue(payload, PdfUploadDTO.class);

        MultipartFile queuedFile = new ByteArrayMultipartFile(
                upload.originalFilename(),
                upload.contentType(),
                upload.fileBytes());

        return pdfUploadService.upload(queuedFile, upload.chapterPageRanges());
    }
}
