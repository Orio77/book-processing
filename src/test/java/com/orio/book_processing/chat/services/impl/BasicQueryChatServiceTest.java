package com.orio.book_processing.chat.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class BasicQueryChatServiceTest {

    @Test
    void generateChatResponse_returnsModelText() throws LLMGenerationException {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage("out"))))
                .build());

        BasicQueryChatService svc = new BasicQueryChatService(chatModel);
        assertEquals("out", svc.generateChatResponse("ctx", "q", "chapter"));
    }

    @Test
    void generateChatResponse_nullPointerFromModel_wrappedAsLLMGenerationException() {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenThrow(new NullPointerException("npe"));

        BasicQueryChatService svc = new BasicQueryChatService(chatModel);
        assertThrows(LLMGenerationException.class, () -> svc.generateChatResponse("a", "b", "c"));
    }

    @Test
    void generateChatResponse_veryLongContext_stillReturnsModelText() throws LLMGenerationException {
        ChatModel chatModel = mock(ChatModel.class, withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS));
        when(chatModel.call(any(Prompt.class))).thenReturn(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage("ok"))))
                .build());

        String longContext = "x".repeat(5000);
        BasicQueryChatService svc = new BasicQueryChatService(chatModel);
        assertEquals("ok", svc.generateChatResponse(longContext, "q", "chapter"));
    }
}
