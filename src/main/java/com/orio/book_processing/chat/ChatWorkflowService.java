package com.orio.book_processing.chat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.services.ExplanationChatService;
import com.orio.book_processing.chat.services.QueryChatService;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWorkflowService {

    private final QueryChatService queryChatService;
    private final ExplanationChatService explanationChatService;
    private final ChapterService chapterService;
    private final ChatResponseService chatResponseService;

    public String chat(List<ChatContextSentenceDTO> context, String query, Long chapterId)
            throws LLMGenerationException {
        log.info("Chat request received for chapter {}: {}", chapterId, query);
        String contextText = getContext(context);
        String chapterText = chapterService.getChapter(chapterId).getText();

        String response = !(query == null || query.isBlank())
                ? queryChatService.generateChatResponse(contextText, query, chapterText)
                : explanationChatService.generateChatResponse(contextText, chapterText);

        chatResponseService.save(chapterId, query, response, context);

        return response;
    }

    private String getContext(List<ChatContextSentenceDTO> context) {
        return context.stream().map(ChatContextSentenceDTO::sentenceContent).collect(Collectors.joining(""));
    }

}
