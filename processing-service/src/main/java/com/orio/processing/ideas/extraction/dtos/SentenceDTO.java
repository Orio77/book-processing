package com.orio.processing.ideas.extraction.dtos;

import com.orio.proto.books.Sentence;

/**
 * Lightweight representation of a source sentence (owned by books-service).
 */
public record SentenceDTO(Long id, String content) {

    public static SentenceDTO from(Sentence sentence) {
        return new SentenceDTO(sentence.getId(), sentence.getContent());
    }

    /** Same shape the extraction prompt used when sentences were entities. */
    @Override
    public String toString() {
        return "{%s: \"%s\"}".formatted(this.id, this.content);
    }
}
