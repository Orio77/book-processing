package com.orio.processing.ideas.extraction.dtos;

import com.orio.processing.ideas.extraction.models.IdeaArgument;

/**
 * Lightweight API representation of an extracted idea argument.
 */
public record IdeaArgumentDTO(Long id, String text) {

    public static IdeaArgumentDTO from(IdeaArgument ideaArgument) {
        return new IdeaArgumentDTO(ideaArgument.getId(), ideaArgument.getText());
    }
}
