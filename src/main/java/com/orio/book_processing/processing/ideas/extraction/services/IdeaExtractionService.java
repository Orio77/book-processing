package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExtractionService {

    private final ChatModel chatModel;

    private static final String EXTRACTION_PROMPT = """
                Extract core ideas from the given text.

                TEXT:
                \"""
                %s
                \"""

                Respond in the following format:
                %s
            """;

    public List<Idea> getIdeas(String chapterText) {
        BeanOutputConverter<List<Idea>> outConv = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Idea>>() {
                });

        log.info("Extracting ideas...");
        // perform idea extraction here
        ChatResponse response = chatModel
                .call(new Prompt(EXTRACTION_PROMPT.formatted(chapterText, outConv.getFormat())));

        log.debug("Response received: \n\n{}\n\n", response.getResult().getOutput().getText());

        log.info("Converting the JSON response to objects...");
        List<Idea> ideas = outConv.convert(response.getResult().getOutput().getText());
        log.info("Created {} idea objects from the JSON response", ideas.size());
        return ideas.stream().map(idea -> {
            idea.setId(null);
            return idea;
        }).toList();
    }
}
