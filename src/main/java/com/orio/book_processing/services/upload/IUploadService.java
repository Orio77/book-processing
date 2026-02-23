package com.orio.book_processing.services.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.exceptions.FileContentException;
import com.orio.book_processing.exceptions.PDFLoadingException;

public interface IUploadService {

    Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws FileContentException, PDFLoadingException;

}
