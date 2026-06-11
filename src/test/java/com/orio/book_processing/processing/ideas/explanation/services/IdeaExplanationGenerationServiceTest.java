package com.orio.book_processing.processing.ideas.explanation.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

@ExtendWith(MockitoExtension.class)
class IdeaExplanationGenerationServiceTest {

    private static final String LOGIC_MAPPER_JSON = """
            {
              "reasoning": "First the text supports the claim.",
              "explanation": "The author argues this because evidence aligns."
            }
            """;

    @Test
    void generateIdeaExplanation_appendsReasoningAndExplanationFromModelJson() {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage(LOGIC_MAPPER_JSON))))
                .build());

        IdeaExplanationGenerationService svc = new IdeaExplanationGenerationService(chatModel);

        String text = svc.generateIdeaExplanation("Concept", List.of("p1", "p2"), "chapter body");

        assertEquals(
                "First the text supports the claim.\nThe author argues this because evidence aligns.",
                text);
    }
}
