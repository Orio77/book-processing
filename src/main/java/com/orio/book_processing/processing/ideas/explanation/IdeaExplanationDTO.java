package com.orio.book_processing.processing.ideas.explanation;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

public record IdeaExplanationDTO(Long id, Long ideaId, String content) {

    public static IdeaExplanationDTO from(Long id, Idea idea, String content) {
        return new IdeaExplanationDTO(id, idea.getId(), content);
    }
}
