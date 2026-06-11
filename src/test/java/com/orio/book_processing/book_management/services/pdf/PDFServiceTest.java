package com.orio.book_processing.book_management.services.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.repositories.PDFRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PDFServiceTest {

    @Mock
    private PDFRepository pdfRepo;

    @InjectMocks
    private PDFService pdfService;

    @Test
    void createPDF_usesFilenameAndPageCount() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            byte[] bytes = new byte[] { 1, 2 };
            var file = new MockMultipartFile("f", "report.pdf", "application/pdf", bytes);
            PDF pdf = pdfService.createPDF(doc, file, bytes);
            assertEquals("report.pdf", pdf.getTitle());
            assertEquals(1, pdf.getTotalPages());
            assertSame(bytes, pdf.getContent());
        }
    }

    @Test
    void createPDF_blankFilename_defaultsToUnknown() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            byte[] bytes = new byte[0];
            var file = new MockMultipartFile("f", "  ", "application/pdf", bytes);
            PDF pdf = pdfService.createPDF(doc, file, bytes);
            assertEquals("unknown", pdf.getTitle());
        }
    }

    @Test
    void createPDF_nullOriginalFilename_defaultsToUnknown() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new org.apache.pdfbox.pdmodel.PDPage());
            byte[] bytes = new byte[0];
            var file = new MockMultipartFile("f", null, "application/pdf", bytes);
            PDF pdf = pdfService.createPDF(doc, file, bytes);
            assertEquals("unknown", pdf.getTitle());
        }
    }

    @Test
    void savePDF_returnsPersisted() {
        PDF in = new PDF();
        PDF out = new PDF();
        when(pdfRepo.saveAndFlush(in)).thenReturn(out);
        assertSame(out, pdfService.savePDF(in));
        verify(pdfRepo).saveAndFlush(in);
    }

    @Test
    void getPdf_found() {
        PDF pdf = new PDF();
        when(pdfRepo.findById(1L)).thenReturn(Optional.of(pdf));
        assertSame(pdf, pdfService.getPdf(1L));
    }

    @Test
    void getPdf_missing_throws() {
        when(pdfRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> pdfService.getPdf(1L));
    }

    @Test
    void getAllPdfs_delegates() {
        List<PDF> list = List.of(new PDF());
        when(pdfRepo.findAll()).thenReturn(list);
        assertSame(list, pdfService.getAllPdfs());
    }

    @Test
    void deletePDF_missing_returnsFalse() {
        when(pdfRepo.existsById(9L)).thenReturn(false);
        assertFalse(pdfService.deletePDF(9L));
    }

    @Test
    void deletePDF_existing_deletesAndReturnsTrue() {
        when(pdfRepo.existsById(9L)).thenReturn(true);
        assertTrue(pdfService.deletePDF(9L));
        verify(pdfRepo).deleteById(9L);
    }
}
