package com.orio.book_processing.book_management.services.pdf;

import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.repositories.PDFRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PDFService {

    private final PDFRepository pdfRepo;
    private final UserRepository userRepo;

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

    public PDF savePDFForUser(PDF pdf, Long userId) {
        log.info("Saving pdf {}...", pdf.getTitle());
        User user = userRepo.getReferenceById(userId);
        pdf.setUser(user);
        PDF savedPDF = pdfRepo.saveAndFlush(pdf);
        log.info("Saved pdf {} with id: {}", pdf.getTitle(), pdf.getId());
        return savedPDF;
    }

    @Transactional(readOnly = true)
    public PDF getPdf(Long pdfId, Long userId) throws EntityNotFoundException {
        return pdfRepo.findByIdAndUserId(pdfId, userId)
                .orElseThrow(() -> new EntityNotFoundException("PDF not found with id: " + pdfId));
    }

    @Transactional(readOnly = true)
    public List<PDF> getAllPdfs(Long userId) {
        return pdfRepo.findAllByUserId(userId);
    }

    @Transactional
    public boolean deletePDF(Long pdfId, Long userId) {
        log.info("Deleting PDF {}...", pdfId);
        if (!pdfRepo.existsById(pdfId)) {
            log.warn("PDf with id {} doesn't exist, returning.", pdfId);
            return false;
        }
        pdfRepo.deleteByIdAndUserId(pdfId, userId);
        log.info("PDF with id {} deleted successfully.", pdfId);
        return true;
    }

}
