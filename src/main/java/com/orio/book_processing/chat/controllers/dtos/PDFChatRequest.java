package com.orio.book_processing.chat.controllers.dtos;

import java.util.List;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.dtos.response.SentenceDTO;

public record PDFChatRequest(Long chapterId, String query, List<SentenceDTO> context) {

    public static PDFChatRequest from(Long chapterId, String query, List<Sentence> sentences) {
        return new PDFChatRequest(chapterId, query, sentences.stream().map(SentenceDTO::from).toList());
    }
}
