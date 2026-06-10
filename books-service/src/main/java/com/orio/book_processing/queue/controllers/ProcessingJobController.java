package com.orio.book_processing.queue.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orio.book_processing.queue.dtos.ProcessingJobPayloads.ChapterSummaryRequest;
import com.orio.book_processing.queue.dtos.ProcessingJobPayloads.IdeaExplanationRequest;
import com.orio.book_processing.queue.dtos.ProcessingJobPayloads.IdeaExtractionRequest;
import com.orio.book_processing.queue.dtos.ProcessingJobPayloads.IdeasExplanationRequest;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enqueue endpoints for LLM jobs executed by processing-service. The gateway
 * routes POST /api/pdf/process/** here (enqueue = persist a PENDING Job);
 * GET/PUT/DELETE on the same paths go to processing-service.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process")
public class ProcessingJobController {

    private final JobDispatcher jobDispatcher;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @PostMapping("/chapter/summary")
    public ResponseEntity<?> chapterSummary(@RequestParam Long chapterId, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = currentUserId(jwt);
            Long jobId = jobDispatcher.enqueue(JobType.CHAPTER_SUMMARY, new ChapterSummaryRequest(chapterId, userId),
                    userId);
            return ResponseEntity.accepted().body(jobId);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/idea/extract")
    public ResponseEntity<?> extractIdeasByChapterId(@RequestParam Long chapterId, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = currentUserId(jwt);
            Long jobId = jobDispatcher.enqueue(JobType.IDEA_EXTRACTION, new IdeaExtractionRequest(chapterId, userId),
                    userId);
            return ResponseEntity.accepted().body(jobId);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/idea/{ideaId}/explanation")
    public ResponseEntity<?> createExplanation(@PathVariable Long ideaId,
            @RequestBody String ideaContent, @AuthenticationPrincipal Jwt jwt) {
        log.info("Explanation creation request received for idea {}.", ideaId);
        if (ideaContent == null || ideaContent.isBlank()) {
            return ResponseEntity.badRequest().body("Explanation content must not be blank.");
        }

        try {
            Long userId = currentUserId(jwt);
            Long jobId = jobDispatcher.enqueue(JobType.IDEA_EXPLANATION,
                    new IdeaExplanationRequest(ideaId, ideaContent, userId), userId);
            return ResponseEntity.accepted().body(jobId);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/idea/{chapterId}/explanations")
    public ResponseEntity<?> createExplanations(@PathVariable Long chapterId, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = currentUserId(jwt);
            Long jobId = jobDispatcher.enqueue(JobType.IDEAS_EXPLANATION,
                    new IdeasExplanationRequest(chapterId, userId), userId);
            return ResponseEntity.accepted().body(jobId);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }

}
