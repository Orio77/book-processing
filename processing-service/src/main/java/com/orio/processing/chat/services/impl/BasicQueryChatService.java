package com.orio.processing.chat.services.impl;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.orio.processing.chat.services.QueryChatService;
import com.orio.processing.core.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class BasicQueryChatService implements QueryChatService {

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
            log.debug("Calling {} with prompt:\n\n{}\n\n", chatModel.getDefaultOptions().getModel(),
                    prompt.getContents().substring(0, Math.min(prompt.getContents().length(), 2000)));
            return chatModel.call(prompt).getResult()
                    .getOutput()
                    .getText();
        } catch (NullPointerException e) {
            log.error("LLM call failed with NullPointerException", e);
            throw new LLMGenerationException(e.getMessage(), e.getCause());
        }
    }
}
