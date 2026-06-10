
package com.orio.processing.chat.services.impl;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.orio.processing.chat.services.ExplanationChatService;
import com.orio.processing.core.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class BasicExplanationChatService implements ExplanationChatService {

    private final ChatModel chatModel;

    private static final String EXPLANATION_PROMPT = """
            Reader didn't understand something about the following fragment:
            \"""
            %s
            \"""

            Explain it properly and thoroughly

            In the context of this chapter:
            \"""
            %s
            \"""
            """;

    @Override
    public String generateChatResponse(String sentenceContext, String chapterText)
            throws LLMGenerationException {
        try {
            log.info("Calling {} for an explanation...", chatModel.getDefaultOptions().getModel());
            Prompt prompt = new Prompt(EXPLANATION_PROMPT.formatted(sentenceContext, chapterText));
            log.debug("Calling {} with prompt:\n\n{}\n\n", chatModel.getDefaultOptions().getModel(), prompt);
            return chatModel
                    .call(prompt)
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (NullPointerException e) {
            log.error("LLM call failed with NullPointerException", e);
            throw new LLMGenerationException(e.getMessage(), e.getCause());
        }
    }
}
