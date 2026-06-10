package com.orio.processing.core.exceptions;

/**
 * Thrown when an LLM-backed operation fails to produce a usable response.
 */
public class LLMGenerationException extends RuntimeException {

    public LLMGenerationException(String message) {
        super(message);
    }

    public LLMGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
