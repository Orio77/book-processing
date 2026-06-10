package com.orio.processing.ideas.extraction.dtos;

import com.orio.processing.ideas.extraction.models.Idea;

/**
 * Lightweight API representation of an extracted idea.
 */
public record IdeaDTO(Long id, String title) {

    public static IdeaDTO from(Idea idea) {
        return new IdeaDTO(idea.getId(), idea.getTitle());
    }
}
