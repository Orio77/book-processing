package com.orio.book_processing.chat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.chat.services.IExplanationChatService;
import com.orio.book_processing.chat.services.IQueryChatService;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.ideas.extraction.models.dtos.response.SentenceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatWorkflowService {

    private final IQueryChatService queryChatService;
    private final IExplanationChatService explanationChatService;
    private final ChapterService chapterService;
    private final ChatResponseService chatResponseService;

    public String chat(List<SentenceDTO> context, String query, Long chapterId) throws LLMGenerationException {
        log.info("Chat request received for chapter {}: {}\n", chapterId, query);
        String contextStr = getContext(context);
        String chapterText = chapterService.getChapter(chapterId).getText();

        String response = !(query == null || query.isBlank())
                ? queryChatService.generateChatResponse(contextStr, query, chapterText)
                : explanationChatService.generateChatResponse(contextStr, chapterText);

        chatResponseService.save(chapterId, query, response, context);

        return response;
    }

    private String getContext(List<SentenceDTO> context) {
        return context.stream().map(SentenceDTO::sentenceContent).collect(Collectors.joining("\n"));
    }

}
