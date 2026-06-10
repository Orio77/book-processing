package com.orio.processing.queue;

/**
 * LLM job types handled by processing-service. Names must match the JobType
 * enum of books-service — they travel as strings over gRPC.
 */
public enum JobType {
    CHAPTER_SUMMARY, CHAT, IDEA_EXTRACTION, IDEA_EXPLANATION, IDEAS_EXPLANATION
}
