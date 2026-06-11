package com.orio.book_processing.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class MockLLMResponseConfigTest {

    @Test
    void beans_returnNonEmptyPayloads() {
        MockLLMResponseConfig cfg = new MockLLMResponseConfig();
        assertTrue(cfg.extractedIdeas().contains("ideaContainers"));
        assertTrue(cfg.explanationAIChatResponse().length() > 50);
        assertTrue(cfg.queryChatResponse().length() > 50);
    }

    @Test
    void extractedIdeas_isValidJsonWithIdeaContainersArray() throws Exception {
        MockLLMResponseConfig cfg = new MockLLMResponseConfig();
        JsonNode root = new ObjectMapper().readTree(cfg.extractedIdeas());
        assertTrue(root.path("ideaContainers").isArray());
        assertTrue(root.path("ideaContainers").size() > 0);
        assertEquals("Truthful responses enable respectful resolution in ambiguous social situations",
                root.path("ideaContainers").path(0).path("ideaTitle").asText());
    }

    @Test
    void chatResponseBeans_areDistinctLongFormAssistantPayloads() {
        MockLLMResponseConfig cfg = new MockLLMResponseConfig();
        String explanation = cfg.explanationAIChatResponse();
        String query = cfg.queryChatResponse();
        assertTrue(explanation.contains("Peterson"));
        assertTrue(query.contains("William James"));
        assertNotEquals(explanation, query);
    }
}
