package com.orio.book_processing.chat.services;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

/**
 * Contract for generating explanatory chat responses without a direct query.
 */
public interface ExplanationChatService {

    String generateChatResponse(String sentenceContext, String chapterText) throws LLMGenerationException;
}
