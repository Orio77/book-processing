package com.orio.book_processing.processing.ideas.extraction.dtos;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

public record IdeaDTO(Long id, String title) {

    public static IdeaDTO from(Idea idea) {
        return new IdeaDTO(idea.getId(), idea.getTitle());
    }
}
