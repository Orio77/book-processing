package com.orio.book_processing.chat.dtos;

import com.orio.book_processing.book_management.models.Sentence;

/**
 * Sentence payload selected as context for a chat request.
 */
public record ChatContextSentenceDTO(Long sentenceId, String sentenceContent) {

    public static ChatContextSentenceDTO from(Sentence sentence) {
        return new ChatContextSentenceDTO(sentence.getId(), sentence.getContent());
    }

}
