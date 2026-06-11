package com.orio.book_processing.chat.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MockExplanationChatServiceTest {

    @Test
    void returnsConfiguredExplanationIgnoringInputs() {
        MockExplanationChatService svc = new MockExplanationChatService("fixed-explanation");
        assertEquals("fixed-explanation", svc.generateChatResponse("any-context", "any-chapter"));
    }
}
