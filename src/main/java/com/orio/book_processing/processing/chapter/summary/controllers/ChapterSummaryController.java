package com.orio.book_processing.processing.chapter.summary.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orio.book_processing.processing.chapter.summary.dtos.ChapterSummaryResponse;
import com.orio.book_processing.processing.chapter.summary.services.wrappers.ChapterSummaryService;
import com.orio.book_processing.queue.Job.JobType;
import com.orio.book_processing.queue.JobDispatcher;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for chapter processing operations such as summary generation
 * and retrieval.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process")
public class ChapterSummaryController {

    private final ChapterSummaryService chapterSummaryService;
    private final JobDispatcher jobDispatcher;

    @PostMapping("/chapter/summary")
    public ResponseEntity<?> chapterSummary(@RequestParam Long chapterId) {
        try {
            Long jobId = jobDispatcher.enqueue(JobType.CHAPTER_SUMMARY, chapterId);
            return ResponseEntity.accepted().body(jobId);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e);
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
