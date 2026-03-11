package com.orio.book_processing.chat;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
