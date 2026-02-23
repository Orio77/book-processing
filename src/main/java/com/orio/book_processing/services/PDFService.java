package com.orio.book_processing.services;

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

    public PDF savePDF(PDDocument doc, MultipartFile file, byte[] fileBytes) {
        PDF pdf = new PDF();
        String title = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) ? "unknown"
                : file.getOriginalFilename();

        pdf.setTitle(title);
        pdf.setTotalPages(doc.getNumberOfPages());
        pdf.setContent(fileBytes);

        return pdfRepo.saveAndFlush(pdf);
    }

}
