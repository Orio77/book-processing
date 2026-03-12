package com.orio.book_processing.chat.services.impl;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.orio.book_processing.chat.services.IQueryChatService;
import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class QueryChatService implements IQueryChatService {

    private final ChatModel chatModel;

    private static final String CHAT_PROMPT = """
            Reading the following fragment:
            \"""
            %s
            \"""

            User has the following query:
            \"""
            %s
            \"""

            In the context of this chapter:
            \"""
            %s
            \"""
            """;

    @Override
    public String generateChatResponse(String sentenceContext, String query, String chapterText)
            throws LLMGenerationException {
        try {
            log.info("Sending query to {}...", chatModel.getDefaultOptions().getModel());
            Prompt prompt = new Prompt(CHAT_PROMPT.formatted(sentenceContext, query, chapterText));
            log.debug("Calling {} with prompt:\n\n{}\n\n", chatModel.getDefaultOptions().getModel(), prompt);
            return chatModel.call(prompt).getResult()
                    .getOutput()
                    .getText();
        } catch (NullPointerException e) {
            log.error("Response from the LLM was null");
            throw new LLMGenerationException(e.getMessage(), e.getCause());
        }
    }
}
