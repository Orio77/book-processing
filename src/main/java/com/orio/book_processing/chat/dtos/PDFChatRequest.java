package com.orio.book_processing.chat.dtos;

import java.util.List;

import com.orio.book_processing.book_management.models.Sentence;

/**
 * API request payload for a chapter chat interaction.
 */
public record PDFChatRequest(Long chapterId, String query, List<ChatContextSentenceDTO> context) {

    public static PDFChatRequest from(Long chapterId, String query, List<Sentence> sentences) {
        return new PDFChatRequest(chapterId, query, sentences.stream().map(ChatContextSentenceDTO::from).toList());
    }
}
