package com.orio.book_processing;

import java.util.List;

import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.chat.dtos.PDFChatResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SampleTestDataFactory {

    public static final Long DEFAULT_SENTENCE_ID = 1L;
    public static final String DEFAULT_SENTENCE_CONTENT = "Sentence content";
    public static final Long DEFAULT_CHAPTER_ID = 10L;
    public static final String DEFAULT_QUERY = "Explain the topic";

    public static ChatContextSentenceDTO chatContextSentenceDTO() {
        return new ChatContextSentenceDTO(DEFAULT_SENTENCE_ID, DEFAULT_SENTENCE_CONTENT);
    }

    public static ChatContextSentenceDTO chatContextSentenceDTO(Long sentenceId, String sentenceContent) {
        return new ChatContextSentenceDTO(sentenceId, sentenceContent);
    }

    public static PDFChatRequest pdfChatRequest() {
        return new PDFChatRequest(DEFAULT_CHAPTER_ID, DEFAULT_QUERY, List.of(chatContextSentenceDTO()), null);
    }

    public static PDFChatRequest pdfChatRequest(Long userId) {
        return new PDFChatRequest(DEFAULT_CHAPTER_ID, DEFAULT_QUERY, List.of(chatContextSentenceDTO()), userId);
    }

    public static PDFChatRequest pdfChatRequestWithEmptyContext() {
        return new PDFChatRequest(DEFAULT_CHAPTER_ID, DEFAULT_QUERY, List.of(), null);
    }

    public static PDFChatRequest pdfChatRequest(Long chapterId, String query, List<ChatContextSentenceDTO> context, Long userId) {
        return new PDFChatRequest(chapterId, query, context, userId);
    }

    public static PDFChatResponse pdfChatResponse(String query, Long chatResponseId, String chatResponse, List<Long> contextSentencesIds) {
        return new PDFChatResponse(query, chatResponseId, chatResponse, contextSentencesIds);
    }
}
