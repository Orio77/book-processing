package com.orio.book_processing.core.config;

import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Primary
@Component
@Profile("mock")
public class MockChatModel implements ChatModel {

    @Override
    public ChatResponse call(Prompt prompt) {
        return ChatResponse.builder().generations(List.of(new Generation(new AssistantMessage("LLM Mocked Response"))))
                .build();
    }

}
