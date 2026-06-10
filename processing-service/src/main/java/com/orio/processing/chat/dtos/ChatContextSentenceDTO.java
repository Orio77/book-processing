package com.orio.processing.chat.dtos;

/**
 * Sentence payload selected as context for a chat request. Mirrors the JSON
 * shape produced by books-service when enqueuing CHAT jobs.
 */
public record ChatContextSentenceDTO(Long sentenceId, String sentenceContent) {

}
