package com.orio.book_processing.processing.ideas.extraction.models;

public record IdeaDTO(Long ideaId, String ideaTitle) {

    public static IdeaDTO from(Idea idea) {
        return new IdeaDTO(idea.getId(), idea.getTitle());
    }
}
