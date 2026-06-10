package com.orio.processing.ideas.extraction.dtos;

import java.util.List;

/**
 * API payload combining an extracted idea with supporting sentences.
 */
public record IdeaWithSentences(IdeaDTO idea, List<SentenceDTO> sentences) {

}
