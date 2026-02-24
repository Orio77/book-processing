package com.orio.book_processing.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.dtos.response.ChapterResponse;
import com.orio.book_processing.dtos.response.PdfResponse;
import com.orio.book_processing.dtos.response.SentenceResponse;
import com.orio.book_processing.exceptions.FileContentException;
import com.orio.book_processing.exceptions.PDFLoadingException;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.services.pdf.PDFService;
import com.orio.book_processing.services.upload.IUploadService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = { "http://localhost:5173, http://localhost:5174", "http://localhost:5175" })
@RequiredArgsConstructor
public class PDFController {

    private final IUploadService uploadService;
    private final PDFService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestPart("file") MultipartFile file,
            @RequestPart("chapterPageRanges") List<PageRange> chapterPageRanges) {
        Long pdfId;
        try {
            pdfId = uploadService.upload(file, chapterPageRanges);
        } catch (FileContentException e) {
            return ResponseEntity.internalServerError().body("Failed to read file content: " + e.getMessage());
        } catch (PDFLoadingException e) {
            return ResponseEntity.internalServerError().body("Failed to load PDF: " + e.getMessage());
        }
        return ResponseEntity.ok(pdfId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PdfResponse> getPdf(@PathVariable Long id) {
        try {
            PDF pdf = pdfService.getPdf(id);
            return ResponseEntity.ok(PdfResponse.from(pdf));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<PdfResponse>> getAllPdfs(@RequestParam(defaultValue = "false") Boolean includeContent) {
        List<PDF> pdfs = pdfService.getAllPdfs();

        return pdfs.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(pdfs.stream().map(PdfResponse::from).toList());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletepdf(@PathVariable Long id) {

        boolean isDeleted = pdfService.deletePDF(id);
        if (isDeleted) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/chapter/get/{id}")
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/chapter/get/all/{pdfId}")
    public ResponseEntity<List<ChapterResponse>> getAllChaptersByPdf(@PathVariable Long pdfId) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/sentence/get/{pdfId}")
    public ResponseEntity<List<SentenceResponse>> getSentencesInRange(@ModelAttribute PageRange pageRange,
            @PathVariable Long pdfId) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/sentence/get/ranges/{pdfId}")
    public ResponseEntity<List<List<SentenceResponse>>> getSentencesInRanges(
            @RequestBody List<PageRange> ranges, @PathVariable Long pdfId) {
        return ResponseEntity.ok(List.of());
    }

}
