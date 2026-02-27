package com.orio.book_processing.book_management.services.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.exceptions.FileContentException;
import com.orio.book_processing.book_management.exceptions.PDFLoadingException;

public interface IUploadService {

    Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws FileContentException, PDFLoadingException;

}
