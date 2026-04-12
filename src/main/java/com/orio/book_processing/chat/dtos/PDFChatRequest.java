package com.orio.book_processing.chat.dtos;

import java.util.List;

import com.orio.book_processing.book_management.models.Sentence;

/**
 * API request payload for a chapter chat interaction.
 */
public record PDFChatRequest(Long chapterId, String query, List<ChatContextSentenceDTO> context, Long userId) {

    public static PDFChatRequest from(Long chapterId, String query, List<Sentence> sentences, Long userId) {
        return new PDFChatRequest(chapterId, query, sentences.stream().map(ChatContextSentenceDTO::from).toList(),
                userId);
    }

    public static PDFChatRequest from(PDFChatRequest chatRequest, Long userId) {
        return new PDFChatRequest(chatRequest.chapterId(), chatRequest.query(), chatRequest.context(), userId);
    }
}
