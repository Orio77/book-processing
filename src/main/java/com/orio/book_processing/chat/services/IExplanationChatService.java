package com.orio.book_processing.chat.services;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

public interface IExplanationChatService {

    String generateChatResponse(String sentenceContext, String chapterText) throws LLMGenerationException;
}
