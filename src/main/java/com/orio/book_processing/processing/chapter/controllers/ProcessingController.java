package com.orio.book_processing.processing.chapter.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.core.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.chapter.ChapterSummaryWorkflow;
import com.orio.book_processing.processing.chapter.dtos.ChapterSummaryResponse;
import com.orio.book_processing.processing.chapter.services.ChapterSummaryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process")
public class ProcessingController {

    private final ChapterSummaryWorkflow chapterSummaryWorkflow;
    private final ChapterSummaryService chapterSummaryService;

    @PostMapping("/chapter/summary")
    public ResponseEntity<Long> chapterSummary(@RequestParam Long chapterId) {
        try {
            return ResponseEntity.ok(chapterSummaryWorkflow.generateChapterSummary(chapterId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (LLMGenerationException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/chapter/{chapterId}/summary")
    public ResponseEntity<List<ChapterSummaryResponse>> getSummaryByChapterId(@PathVariable Long chapterId) {
        return chapterSummaryService.findByChapterId(chapterId)
                .map(summaries -> summaries.stream().map(ChapterSummaryResponse::from).toList())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chapter/summary/{id}")
    public ResponseEntity<ChapterSummaryResponse> getChapterSummary(@PathVariable Long id) {
        return chapterSummaryService.getReferenceById(id)
                .map(ChapterSummaryResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("chapter/summary/{id}")
    public ResponseEntity<Boolean> deleteChapterSummary(@PathVariable Long id) {
        if (chapterSummaryService.deleteById(id)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
