package com.orio.book_processing.chat.services;

import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;

public interface IQueryChatService {

    public String generateChatResponse(String sentenceContext, String query, String chapterText)
            throws LLMGenerationException;
}
