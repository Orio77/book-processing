package com.orio.book_processing.queue.dtos;

/**
 * Payload records for LLM jobs executed by processing-service. The JSON shape
 * is the contract — processing-service deserializes matching records.
 */
public final class ProcessingJobPayloads {

    private ProcessingJobPayloads() {
    }

    public record ChapterSummaryRequest(Long chapterId, Long userId) {
    }

    public record IdeaExtractionRequest(Long chapterId, Long userId) {
    }

    public record IdeaExplanationRequest(Long ideaId, String ideaContent, Long userId) {
    }

    public record IdeasExplanationRequest(Long chapterId, Long userId) {
    }
}
