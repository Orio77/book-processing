package com.orio.book_processing.processing.chapter.summary.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.orio.book_processing.core.exceptions.LLMGenerationException;

@ExtendWith(MockitoExtension.class)
class BasicSummaryServiceTest {

    @Test
    void generateChapterSummary_returnsAssistantText() throws LLMGenerationException {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage("sum"))))
                .build());

        BasicSummaryService svc = new BasicSummaryService(chatModel);
        assertEquals("sum", svc.generateChapterSummary("chapter"));
    }

    @Test
    void generateChapterSummary_nullGeneration_throws() {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        ChatResponse response = mock(ChatResponse.class);
        when(response.getResult()).thenReturn(null);
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        BasicSummaryService svc = new BasicSummaryService(chatModel);
        assertThrows(LLMGenerationException.class, () -> svc.generateChapterSummary("x"));
    }

    @Test
    void generateChapterSummary_nullAssistantText_returnsNull() throws LLMGenerationException {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        AssistantMessage msg = mock(AssistantMessage.class);
        when(msg.getText()).thenReturn(null);
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(msg)))
                .build());

        BasicSummaryService svc = new BasicSummaryService(chatModel);
        assertNull(svc.generateChapterSummary("c"));
    }
}
