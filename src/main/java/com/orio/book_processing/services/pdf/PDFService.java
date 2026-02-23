package com.orio.book_processing.services.pdf;

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
        String originalFilename = file.getOriginalFilename();
        String title = (originalFilename == null || originalFilename.isBlank())
                ? "unknown"
                : originalFilename;

        pdf.setTitle(title);
        pdf.setTotalPages(doc.getNumberOfPages());
        pdf.setContent(fileBytes);

        return pdfRepo.saveAndFlush(pdf);
    }

}
