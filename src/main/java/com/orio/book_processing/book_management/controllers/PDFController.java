package com.orio.book_processing.book_management.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.dtos.request.PdfUploadDTO;
import com.orio.book_processing.book_management.dtos.response.ChapterResponse;
import com.orio.book_processing.book_management.dtos.response.PdfResponse;
import com.orio.book_processing.book_management.dtos.response.SentenceResponse;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.book_management.services.pdf.PDFService;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for uploading PDFs and querying PDF, chapter, and sentence
 * data.
 */
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PDFController {

    private final PDFService pdfService;
    private final ChapterService chapterService;
    private final SentenceService sentenceService;

    private final JobDispatcher jobDispatcher;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestPart("file") MultipartFile file,
            @RequestPart("chapterPageRanges") List<PageRange> chapterPageRanges) {
        try {
            Long jobId = jobDispatcher.enqueue(JobType.PDF_UPLOAD, new PdfUploadDTO(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    chapterPageRanges));
            return ResponseEntity.accepted().body(jobId);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Couldn't convert JSON to object");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to read file content: " + e.getMessage());
        }
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
    public ResponseEntity<Boolean> deletePdf(@PathVariable Long id) {

        boolean isDeleted = pdfService.deletePDF(id);
        if (isDeleted) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/chapter/get/{id}")
    public ResponseEntity<ChapterResponse> getChapter(@PathVariable Long id) {

        try {
            Chapter chapter = chapterService.getChapter(id);
            return ResponseEntity.ok(ChapterResponse.from(chapter));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/chapter/get/all/{pdfId}")
    public ResponseEntity<List<ChapterResponse>> getAllChaptersByPdf(@PathVariable Long pdfId) {
        List<Chapter> chapters = chapterService.getAllChapters(pdfId);

        return chapters.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(chapters.stream().map(ChapterResponse::from).toList());
    }

    @GetMapping("/sentence/get/{pdfId}")
    public ResponseEntity<List<SentenceResponse>> getSentencesInRange(@ModelAttribute PageRange pageRange,
            @PathVariable Long pdfId) {
        List<Sentence> sentences = sentenceService.getSentencesInRange(pageRange, pdfId);
        return sentences.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(sentences.stream().map(SentenceResponse::from).toList());
    }

    @PostMapping("/sentence/get/ranges/{pdfId}")
    public ResponseEntity<List<List<SentenceResponse>>> getSentencesInRanges(
            @RequestBody List<PageRange> ranges, @PathVariable Long pdfId) {

        List<List<Sentence>> sentenceGroups = sentenceService.getSentencesInRanges(ranges, pdfId);
        return sentenceGroups.isEmpty() ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(sentenceGroups.stream()
                        .map(sentenceGroup -> sentenceGroup.stream().map(SentenceResponse::from).toList()).toList());
    }

}
