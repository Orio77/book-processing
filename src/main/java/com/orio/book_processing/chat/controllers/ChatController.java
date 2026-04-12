package com.orio.book_processing.chat.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.chat.dtos.PDFChatResponse;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

import lombok.RequiredArgsConstructor;

/**
 * REST endpoints for creating, retrieving, updating, and deleting chapter chat
 * interactions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/chat")
public class ChatController {

    private final ChatResponseService chatResponseService;
    private final JobDispatcher jobDispatcher;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @PostMapping()
    public ResponseEntity<?> chat(@RequestBody PDFChatRequest chatRequest, @AuthenticationPrincipal Jwt jwt) {
        try {
            Long userId = currentUserId(jwt);
            PDFChatRequest userChatRequest = PDFChatRequest.from(chatRequest, userId);
            Long jobId = jobDispatcher.enqueue(JobType.CHAT, userChatRequest);
            return ResponseEntity.accepted().body(jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Chat request failed: " + e);
        }
    }

    @GetMapping("/response/get/all/{chapterId}")
    public ResponseEntity<List<PDFChatResponse>> getChatResponsesForChapter(@PathVariable Long chapterId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ResponseEntity.ok(chatResponseService.getChatResponsesForChapter(chapterId, userId));
    }

    @PutMapping("/response/edit/{chatResponseId}")
    public ResponseEntity<PDFChatResponse> updateChatResponse(@PathVariable Long chatResponseId,
            @RequestBody String body, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return chatResponseService.update(chatResponseId, body, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/response/delete/{chatResponseId}")
    public ResponseEntity<Void> deleteChatResponse(@PathVariable Long chatResponseId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return chatResponseService.deleteChatResponse(chatResponseId, userId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
