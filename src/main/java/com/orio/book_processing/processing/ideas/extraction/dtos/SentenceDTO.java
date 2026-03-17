package com.orio.book_processing.processing.ideas.extraction.dtos;

import com.orio.book_processing.book_management.models.Sentence;

public record SentenceDTO(Long id, String content) {

    public static SentenceDTO from(Sentence sentence) {
        return new SentenceDTO(sentence.getId(), sentence.getContent());
    }

}
