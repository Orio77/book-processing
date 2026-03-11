package com.orio.book_processing.chat;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;

import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class MockQueryChatService implements IQueryChatService {

    private final String queryChatResponse;

    @Override
    public String generateChatResponse(String sentenceContext, String query, String chapterText)
            throws LLMGenerationException {
        return queryChatResponse;
    }

}
