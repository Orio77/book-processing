package com.orio.book_processing.processing;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OllamaSummaryService implements ISummaryService {

    @Override
    public String generateChapterSummary(String chapterText) {
        throw new UnsupportedOperationException("Unimplemented method 'generateChapterSummary'");
    }

}
