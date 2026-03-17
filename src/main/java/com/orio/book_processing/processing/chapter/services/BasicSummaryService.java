package com.orio.book_processing.processing.chapter.services;

import java.util.Map;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSummaryService implements ISummaryService {

    private final ChatModel chatModel;

    private static final String CHAPTER_PLACEHOLDER = "{chapter}";

    private final String chapterSummaryStringPrompt = """
                Explain what the author is trying to convey by laying out a map of the following chapter's ideas and painting a clear picture at the end:

                %s
            """
            .formatted(CHAPTER_PLACEHOLDER);

    @Override
    public String generateChapterSummary(String chapterText) throws LLMGenerationException {
        log.info("Generating a chapter summary...");
        Prompt summaryPrompt = new PromptTemplate(chapterSummaryStringPrompt).create(Map.of("chapter", chapterText));
        log.info("Calling {}...", chatModel.getDefaultOptions().getModel());
        ChatResponse response = chatModel.call(summaryPrompt);
        log.info("Received a response from {}", chatModel.getDefaultOptions().getModel());
        Generation result = response.getResult();

        if (result == null) {
            log.warn("Received response from {} was null, stopping.", chatModel.getDefaultOptions().getModel());
            throw new LLMGenerationException("Generation result was null");
        }
        log.info("Chapter summary generated successfully.");
        return result.getOutput().getText();
    }

}
