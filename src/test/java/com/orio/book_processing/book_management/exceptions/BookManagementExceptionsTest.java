package com.orio.book_processing.book_management.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class BookManagementExceptionsTest {

    @Test
    void fileContentException_withCause() {
        Throwable cause = new RuntimeException("x");
        FileContentException ex = new FileContentException("m", cause);
        assertEquals("m", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void fileContentException_messageOnly() {
        FileContentException ex = new FileContentException("only");
        assertEquals("only", ex.getMessage());
    }

    @Test
    void pdfLoadingException_withCause() {
        Throwable cause = new IllegalStateException();
        PDFLoadingException ex = new PDFLoadingException("bad", cause);
        assertEquals("bad", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void pdfLoadingException_messageOnly() {
        assertEquals("x", new PDFLoadingException("x").getMessage());
    }
}
