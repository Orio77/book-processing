package com.orio.book_processing.processing.ideas.extraction.models;

import java.util.List;

/**
 * Structured AI extraction response containing idea containers.
 */
public record IdeaExtractionAiResponse(List<IdeaRequest> ideaContainers) {

    public record IdeaRequest(String ideaTitle, List<String> arguments, List<Long> ideaSentencesIds) {
    }
}
