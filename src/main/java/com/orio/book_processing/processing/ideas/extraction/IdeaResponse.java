package com.orio.book_processing.processing.ideas.extraction;

import java.util.List;

import com.orio.book_processing.book_management.models.Sentence;

public record IdeaResponse(Idea idea, List<Long> sentenceIds) {

    public static IdeaResponse from(Idea idea, List<Sentence> sentences) {
        return new IdeaResponse(idea, sentences.stream().map(Sentence::getId).toList());
    }
}
