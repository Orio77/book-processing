package com.orio.processing.chat.services;

import com.orio.processing.core.exceptions.LLMGenerationException;

/**
 * Contract for generating explanatory chat responses without a direct query.
 */
public interface ExplanationChatService {

    String generateChatResponse(String sentenceContext, String chapterText) throws LLMGenerationException;
}
