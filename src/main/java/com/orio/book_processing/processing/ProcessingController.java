package com.orio.book_processing.processing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process")
public class ProcessingController {

    private final ChapterSummaryWorkflow chapterSummaryWorkflow;

    @PostMapping("/chapter/summary")
    public ResponseEntity<Long> chapterSummary(@RequestParam Long chapterId) {
        try {
            return ResponseEntity.ok(chapterSummaryWorkflow.generateChapterSummary(chapterId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

}
