package com.orio.book_processing.chat.services;

import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;

public interface IExplanationChatService {

    String generateChatResponse(String sentenceContext, String chapterText) throws LLMGenerationException;
}
