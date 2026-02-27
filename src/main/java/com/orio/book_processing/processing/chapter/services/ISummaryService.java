package com.orio.book_processing.processing.chapter.services;

import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;

public interface ISummaryService {

    public String generateChapterSummary(String chapterText) throws LLMGenerationException;
}
