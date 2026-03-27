package com.orio.book_processing.processing.ideas.explanation.dtos;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

public record IdeaExplanationResponse(Long id, Long ideaId, String content) {

    public static IdeaExplanationResponse from(Long id, Idea idea, String content) {
        return new IdeaExplanationResponse(id, idea.getId(), content);
    }
}
