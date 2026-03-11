package com.orio.book_processing.chat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class MockExplanationChatService implements IExplanationChatService {

    @Qualifier("explanationChatResponse")
    private final String explanationChatResponse;

    @Override
    public String generateChatResponse(String sentenceContext, String chapterText) {
        return explanationChatResponse;
    }
}
