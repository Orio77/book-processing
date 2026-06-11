package com.orio.book_processing.processing.ideas.extraction.services;

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

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;

@ExtendWith(MockitoExtension.class)
class BasicIdeaExtractionServiceTest {

    private static final String MINIMAL_IDEAS_JSON = """
            {
              "ideaContainers": [
                {
                  "ideaTitle": "One idea",
                  "arguments": ["because"],
                  "ideaSentencesIds": [1]
                }
              ]
            }
            """;

    @Test
    void getIdeas_callsChatModelAndConvertsJsonToResponse() {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage(MINIMAL_IDEAS_JSON))))
                .build());

        BasicIdeaExtractionService svc = new BasicIdeaExtractionService(chatModel);

        Sentence s = new Sentence();
        s.setId(1L);
        s.setContent("hello");
        IdeaExtractionAiResponse out = svc.getIdeas(List.of(s));

        assertEquals(1, out.ideaContainers().size());
        assertEquals("One idea", out.ideaContainers().getFirst().ideaTitle());
        assertEquals(List.of("because"), out.ideaContainers().getFirst().arguments());
        assertEquals(List.of(1L), out.ideaContainers().getFirst().ideaSentencesIds());
    }
}
