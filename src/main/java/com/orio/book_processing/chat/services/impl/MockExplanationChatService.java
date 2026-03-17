package com.orio.book_processing.chat.services.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.orio.book_processing.chat.services.ExplanationChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockExplanationChatService implements ExplanationChatService {

    @Qualifier("explanationChatResponse")
    private final String explanationChatResponse;

    @Override
    public String generateChatResponse(String sentenceContext, String chapterText) {
        return explanationChatResponse;
    }
}
