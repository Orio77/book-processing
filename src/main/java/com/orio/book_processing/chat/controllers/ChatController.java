package com.orio.book_processing.chat.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
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
import com.orio.book_processing.queue.Job.JobType;
import com.orio.book_processing.queue.JobDispatcher;

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

    @PostMapping()
    public ResponseEntity<?> chat(@RequestBody PDFChatRequest chatRequest) {
        try {
            Long jobId = jobDispatcher.enqueue(JobType.CHAT, chatRequest);
            return ResponseEntity.accepted().body(jobId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Chat request failed: " + e);
        }
    }

    @GetMapping("/response/get/all/{chapterId}")
    public ResponseEntity<List<PDFChatResponse>> getChatResponsesForChapter(@PathVariable Long chapterId) {
        return ResponseEntity.ok(chatResponseService.getChatResponsesForChapter(chapterId));
    }

    @PutMapping("/response/edit/{chatResponseId}")
    public ResponseEntity<PDFChatResponse> updateChatResponse(@PathVariable Long chatResponseId,
            @RequestBody String body) {
        return chatResponseService.update(chatResponseId, body)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/response/delete/{chatResponseId}")
    public ResponseEntity<Void> deleteChatResponse(@PathVariable Long chatResponseId) {
        return chatResponseService.deleteChatResponse(chatResponseId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
