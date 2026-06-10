package com.orio.processing.ideas.extraction.services;

import java.util.List;

import com.orio.processing.ideas.extraction.dtos.SentenceDTO;
import com.orio.processing.ideas.extraction.models.IdeaExtractionAiResponse;

/**
 * Contract for extracting structured ideas from chapter sentences.
 */
public interface IdeaExtractionService {

    public IdeaExtractionAiResponse getIdeas(List<SentenceDTO> sentences);
}
