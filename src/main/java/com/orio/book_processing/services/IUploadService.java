package com.orio.book_processing.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;

public interface IUploadService {

    Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws FileContentException, PDFLoadingException;

}
