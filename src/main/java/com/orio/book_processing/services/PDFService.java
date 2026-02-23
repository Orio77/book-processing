package com.orio.book_processing.services;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.models.PDF;
import com.orio.book_processing.repositories.PDFRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PDFService {

    private final PDFRepository pdfRepo;

    public PDF savePDF(PDDocument doc, MultipartFile file) throws IOException {
        PDF pdf = new PDF();
        String title = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) ? "unknown"
                : file.getOriginalFilename();

        pdf.setTitle(title);
        pdf.setTotalPages(doc.getNumberOfPages());
        pdf.setContent(file.getBytes());

        return pdfRepo.saveAndFlush(pdf);
    }

}
