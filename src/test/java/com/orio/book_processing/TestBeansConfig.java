package com.orio.book_processing;

import org.mockito.Mockito;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestBeansConfig {

    @Bean
    @Primary
    public ChatModel mockChatModel() {
        return Mockito.mock(ChatModel.class);
    }
}
