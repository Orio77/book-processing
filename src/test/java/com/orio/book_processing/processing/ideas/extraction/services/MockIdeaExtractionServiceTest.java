package com.orio.book_processing.processing.ideas.extraction.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;

class MockIdeaExtractionServiceTest {

    private static final String PAYLOAD = """
            {
              "ideaContainers": [
                {
                  "ideaTitle": "Mocked",
                  "arguments": ["a"],
                  "ideaSentencesIds": [9]
                }
              ]
            }
            """;

    @Test
    void getIdeas_convertsConfiguredJsonPayload() {
        MockIdeaExtractionService svc = new MockIdeaExtractionService(PAYLOAD);

        Sentence s = new Sentence();
        s.setId(9L);
        s.setContent("c");

        IdeaExtractionAiResponse out = svc.getIdeas(List.of(s));

        assertEquals("Mocked", out.ideaContainers().getFirst().ideaTitle());
        assertEquals(1, out.ideaContainers().getFirst().arguments().size());
    }
}
