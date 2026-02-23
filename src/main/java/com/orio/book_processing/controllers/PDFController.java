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
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.dtos.request.PdfUploadRequest;
import com.orio.book_processing.dtos.response.ChapterResponse;
import com.orio.book_processing.dtos.response.PdfResponse;
import com.orio.book_processing.dtos.response.SentenceResponse;
import com.orio.book_processing.services.FileContentException;
import com.orio.book_processing.services.IUploadService;
import com.orio.book_processing.services.PDFLoadingException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = { "http://localhost:5174", "http://localhost:5175" })
@RequiredArgsConstructor
public class PDFController {

    private final IUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@ModelAttribute PdfUploadRequest uploadRequest) {
        Long pdfId;
        try {
            pdfId = uploadService.upload(uploadRequest.getFile(), uploadRequest.getChapterPageRanges());
        } catch (FileContentException e) {
            return ResponseEntity.internalServerError().body("Failed to read file content: " + e.getMessage());
        } catch (PDFLoadingException e) {
            return ResponseEntity.internalServerError().body("Failed to load PDF: " + e.getMessage());
        }
        return ResponseEntity.ok(pdfId);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PdfResponse> getPdf(@PathVariable Long id) {

        // return pdf without content unless explicitly required

        return ResponseEntity.ok(null);
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<PdfResponse>> getAllPdfs(@RequestParam(defaultValue = "false") Boolean includeContent) {
        return ResponseEntity.ok(List.of());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletepdf(@PathVariable Long id) {

        // call service that handles pdf deletion, verify pdf is no longer in the db and
        // return the value

        return ResponseEntity.ok(false);
    }

    @GetMapping("/chapter/get/{id}")
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/chapter/get/all/{pdfId}")
    public ResponseEntity<List<ChapterResponse>> getAllChaptersByPdf(@PathVariable Long pdfId) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/sentence/get")
    public ResponseEntity<List<SentenceResponse>> getSentencesInRange(@ModelAttribute PageRange pageRange) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/sentence/get/ranges")
    public ResponseEntity<List<List<SentenceResponse>>> getSentencesInRanges(
            @RequestBody List<PageRange> ranges) {
        return ResponseEntity.ok(List.of());
    }

}
