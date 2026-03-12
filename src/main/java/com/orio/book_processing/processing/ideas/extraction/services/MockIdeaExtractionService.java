package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockIdeaExtractionService implements IIdeaExtractionService {

    @Qualifier("extractedIdeas")
    private final String extractedIdeas;

    @Override
    public IdeaExtractionAiResponse getIdeas(List<Sentence> sentences) {
        log.info("Mocking LLM response for {} sentences", sentences.size());

        // prepare output converter
        BeanOutputConverter<IdeaExtractionAiResponse> outConv = new BeanOutputConverter<>(
                new ParameterizedTypeReference<IdeaExtractionAiResponse>() {
                });

        // call LLM
        ChatResponse response = mockChatResponse();
        log.debug("Mock response received: \n\n{}\n\n", response.getResult().getOutput().getText());

        // convert output
        log.info("Converting the JSON response to objects...");
        return outConv.convert(response.getResult().getOutput().getText());
    }

    private ChatResponse mockChatResponse() {
        return ChatResponse.builder().generations(List.of(new Generation(new AssistantMessage(extractedIdeas))))
                .build();
    }
}
