package com.orio.book_processing.processing.ideas.extraction.models.dtos.response;

import com.orio.book_processing.book_management.models.Sentence;

public record SentenceDTO(Long sentenceId, String sentenceContent) {

    public static SentenceDTO from(Sentence sentence) {
        return new SentenceDTO(sentence.getId(), sentence.getContent());
    }

}
