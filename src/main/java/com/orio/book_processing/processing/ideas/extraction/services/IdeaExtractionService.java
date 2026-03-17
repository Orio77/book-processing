package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;

/**
 * Contract for extracting structured ideas from chapter sentences.
 */
public interface IdeaExtractionService {

    public IdeaExtractionAiResponse getIdeas(List<Sentence> sentences);
}
