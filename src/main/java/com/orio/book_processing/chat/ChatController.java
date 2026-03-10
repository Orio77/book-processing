package com.orio.book_processing.chat;

import org.springframework.http.ResponseEntity;
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

    @PostMapping()
    public ResponseEntity<?> chat(@RequestBody PDFChatRequest chatRequest) {
        try {
            String response = chatWorkflowService.chat(chatRequest.getContext(), chatRequest.getQuery(),
                    chatRequest.getChapterId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("LLM service returned a null response");
        }
    }

    @PostMapping("/explain")
    public ResponseEntity<String> explain(@RequestBody PDFChatRequest explanationRequest) {
        try {
            String response = chatWorkflowService.chat(explanationRequest.getContext(), explanationRequest.getQuery(),
                    explanationRequest.getChapterId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("LLM service returned a null response");
        }
    }

}
