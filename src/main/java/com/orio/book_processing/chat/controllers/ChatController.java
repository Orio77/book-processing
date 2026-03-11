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

import com.orio.book_processing.chat.ChatWorkflowService;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.chat.dtos.PDFChatResponse;
import com.orio.book_processing.chat.services.impl.ChatResponseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/chat")
public class ChatController {

    private final ChatWorkflowService chatWorkflowService;
    private final ChatResponseService chatResponseService;

    @PostMapping()
    public ResponseEntity<?> chat(@RequestBody PDFChatRequest chatRequest) {
        try {
            String response = chatWorkflowService.chat(chatRequest.context(), chatRequest.query(),
                    chatRequest.chapterId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("LLM service returned a null response");
        }
    }

    @PostMapping("/explain")
    public ResponseEntity<String> explain(@RequestBody PDFChatRequest explanationRequest) {
        try {
            String response = chatWorkflowService.chat(explanationRequest.context(),
                    explanationRequest.query(),
                    explanationRequest.chapterId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("LLM service returned a null response");
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
