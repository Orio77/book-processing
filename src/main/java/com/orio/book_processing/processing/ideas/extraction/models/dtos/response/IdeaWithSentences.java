package com.orio.book_processing.processing.ideas.extraction.models.dtos.response;

import java.util.List;

public record IdeaWithSentences(IdeaDTO idea, List<SentenceDTO> sentences) {

}
