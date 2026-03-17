package com.orio.book_processing.book_management.services.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.exceptions.FileContentException;
import com.orio.book_processing.book_management.exceptions.PDFLoadingException;

/**
 * Contract for uploading a PDF and creating its persisted processing metadata.
 */
public interface UploadService {

    Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws FileContentException, PDFLoadingException;

}
