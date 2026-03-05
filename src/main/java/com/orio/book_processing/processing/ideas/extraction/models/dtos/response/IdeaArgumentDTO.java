package com.orio.book_processing.processing.ideas.extraction.models.dtos.response;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;

public record IdeaArgumentDTO(Long id, String text) {

    public static IdeaArgumentDTO from(IdeaArgument ideaArgument) {
        return new IdeaArgumentDTO(ideaArgument.getId(), ideaArgument.getText());
    }
}
