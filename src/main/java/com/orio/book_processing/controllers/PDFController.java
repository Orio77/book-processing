package com.orio.book_processing.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;

@RestController
@RequestMapping("/api/pdf")
public class PDFController {

    @PutMapping("/upload")
    public ResponseEntity<Integer> uploadPdf(@RequestParam MultipartFile file,
            @RequestParam Map<Integer, Integer> chapterPageRanges) {

        // call service that handles pdf upload and return pdf id assigned in the db

        return ResponseEntity.ok(1);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PDF> getPdf(@PathVariable Long id) {

        // return pdf without content unless explicitly required

        return ResponseEntity.ok(new PDF());
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<PDF>> getAllPdfs(@RequestParam(defaultValue = "false") Boolean includeContent) {
        return ResponseEntity.ok(List.of());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deletepdf(@PathVariable Long id) {

        // call service that handles pdf deletion, verify pdf is no longer in the db and
        // return the value

        return ResponseEntity.ok(true);
    }

    @GetMapping("/chapter/get/{id}")
    public ResponseEntity<Chapter> getChapter(@PathVariable Long id) {
        return ResponseEntity.ok(new Chapter());
    }

    @GetMapping("/chapter/get/all/{pdfId}")
    public ResponseEntity<List<Chapter>> getAllChaptersByPdf(@PathVariable Long pdfId) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/sentence/get")
    public ResponseEntity<List<Sentence>> getSentencesInRange(@RequestParam int startPage, @RequestParam int endPage) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/sentence/get/ranges")
    public ResponseEntity<List<List<Sentence>>> getSentencesInRanges(@RequestParam Map<Integer, Integer> ranges) {
        return ResponseEntity.ok(List.of());
    }

}
