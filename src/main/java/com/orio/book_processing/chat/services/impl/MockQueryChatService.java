package com.orio.book_processing.chat.services.impl;

import org.springframework.stereotype.Service;

import com.orio.book_processing.chat.services.IQueryChatService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockQueryChatService implements IQueryChatService {

    private final String queryChatResponse;

    @Override
    public String generateChatResponse(String sentenceContext, String query, String chapterText)
            throws LLMGenerationException {
        return queryChatResponse;
    }

}
