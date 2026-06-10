package com.orio.processing.chapter.summary.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.processing.chapter.summary.dtos.ChapterSummaryResponse;
import com.orio.processing.chapter.summary.services.wrappers.ChapterSummaryService;

import lombok.RequiredArgsConstructor;

/**
 * Read/delete endpoints for chapter summaries. Summary generation is enqueued
 * in books-service (POST /api/pdf/process/** routes there).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process")
public class ChapterSummaryController {

    private final ChapterSummaryService chapterSummaryService;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @GetMapping("/chapter/{chapterId}/summary")
    public ResponseEntity<List<ChapterSummaryResponse>> getSummaryByChapterId(@PathVariable Long chapterId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return chapterSummaryService.findByChapterIdAndUserId(chapterId, userId)
                .map(summaries -> summaries.stream().map(ChapterSummaryResponse::from).toList())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/chapter/summary/{id}")
    public ResponseEntity<ChapterSummaryResponse> getChapterSummary(@PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return chapterSummaryService.findByIdAndUserId(id, userId)
                .map(ChapterSummaryResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("chapter/summary/{id}")
    public ResponseEntity<Boolean> deleteChapterSummary(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        if (chapterSummaryService.deleteByIdAndUserId(id, userId)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
