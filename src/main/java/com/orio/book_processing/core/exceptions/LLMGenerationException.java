package com.orio.book_processing.core.exceptions;

public class LLMGenerationException extends Exception {

    public LLMGenerationException(String message) {
        super(message);
    }

    public LLMGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
