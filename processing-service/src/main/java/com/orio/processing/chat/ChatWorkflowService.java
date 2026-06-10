package com.orio.processing.chat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.orio.processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.processing.chat.services.ExplanationChatService;
import com.orio.processing.chat.services.QueryChatService;
import com.orio.processing.core.exceptions.LLMGenerationException;
import com.orio.processing.grpc.BooksClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Coordinates PDF chat flows: selects query or explanation generation, loads
 * chapter text from books-service over gRPC, and persists the generated
 * response back in books-service (ChatResponse stays owned there).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWorkflowService {

    private final QueryChatService queryChatService;
    private final ExplanationChatService explanationChatService;
    private final BooksClient booksClient;

    public Long chat(List<ChatContextSentenceDTO> context, String query, Long chapterId, Long userId)
            throws LLMGenerationException {
        log.info("Chat request received for chapter {}: {}", chapterId, query);
        String contextText = getContext(context);
        String chapterText = booksClient.getChapterText(chapterId, userId).getText();

        // Null or blank query triggers "explain" mode; otherwise treat as a user
        // question.
        String response = !(query == null || query.isBlank())
                ? queryChatService.generateChatResponse(contextText, query, chapterText)
                : explanationChatService.generateChatResponse(contextText, chapterText);

        List<Long> contextSentenceIds = context.stream().map(ChatContextSentenceDTO::sentenceId).toList();
        return booksClient.saveChatResponse(chapterId, userId, query, response, contextSentenceIds);
    }

    private String getContext(List<ChatContextSentenceDTO> context) {
        return context.stream().map(ChatContextSentenceDTO::sentenceContent).collect(Collectors.joining(""));
    }

}
