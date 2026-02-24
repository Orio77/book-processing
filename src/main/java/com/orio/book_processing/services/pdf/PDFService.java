package com.orio.book_processing.services.pdf;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.models.PDF;
import com.orio.book_processing.repositories.PDFRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PDFService {

    private final PDFRepository pdfRepo;

    public PDF createPDF(PDDocument doc, MultipartFile file, byte[] fileBytes) {
        PDF pdf = new PDF();
        String originalFilename = file.getOriginalFilename();
        String title = (originalFilename == null || originalFilename.isBlank())
                ? "unknown"
                : originalFilename;

        pdf.setTitle(title);
        pdf.setTotalPages(doc.getNumberOfPages());
        pdf.setContent(fileBytes);

        log.info("Created pdf \"{}\" with {} pages", title, doc.getNumberOfPages());

        return pdf;
    }

    public PDF savePDF(PDF pdf) {
        log.info("Saving {}...", pdf.getTitle());
        PDF savedPDF = pdfRepo.saveAndFlush(pdf);
        log.info("Saved {} with id: {}", pdf.getTitle(), pdf.getId());
        return savedPDF;
    }

    public PDF getPdf(Long id) throws EntityNotFoundException {
        return pdfRepo.getReferenceById(id);
    }

    public List<PDF> getAllPdfs() {
        return pdfRepo.findAll();
    }

    public boolean deletePDF(Long id) {

        pdfRepo.deleteById(id);

        try {
            pdfRepo.getReferenceById(id);
            return false;
        } catch (EntityNotFoundException e) {
            return true;
        }
    }

}
