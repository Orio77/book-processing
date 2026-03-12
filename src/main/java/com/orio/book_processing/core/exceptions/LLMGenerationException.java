package com.orio.book_processing.core.exceptions;

public class LLMGenerationException extends Exception {

    public LLMGenerationException(String arg0) {
        super(arg0);
    }

    public LLMGenerationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
