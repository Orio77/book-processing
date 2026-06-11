package com.orio.book_processing.chat.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

class MockQueryChatServiceTest {

    @Test
    void returnsConfiguredResponse() throws LLMGenerationException {
        MockQueryChatService svc = new MockQueryChatService("fixed");
        assertEquals("fixed", svc.generateChatResponse("any", "any", "any"));
    }
}
