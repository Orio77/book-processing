package com.orio.processing.chapter.summary.services;

import com.orio.processing.core.exceptions.LLMGenerationException;

/**
 * Contract for generating a chapter summary from raw chapter text.
 */
public interface ISummaryService {

    public String generateChapterSummary(String chapterText) throws LLMGenerationException;
}
