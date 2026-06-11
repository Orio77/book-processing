package com.orio.book_processing.core.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class LLMGenerationExceptionTest {

    @Test
    void messageConstructor() {
        var e = new LLMGenerationException("m");
        assertEquals("m", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    void messageAndCauseConstructor() {
        var cause = new RuntimeException("c");
        var e = new LLMGenerationException("m", cause);
        assertEquals("m", e.getMessage());
        assertSame(cause, e.getCause());
    }

    @Test
    void isCheckedException_notRuntime() {
        Exception ex = new LLMGenerationException("x");
        assertInstanceOf(Exception.class, ex);
        assertFalse(ex instanceof RuntimeException);
    }
}
