package com.orio.book_processing.chat.services;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

/**
 * Contract for generating query-driven chat responses.
 */
public interface QueryChatService {

    public String generateChatResponse(String sentenceContext, String query, String chapterText)
            throws LLMGenerationException;
}
