package com.orio.book_processing.book_management.exceptions;

import java.io.IOException;

/**
 * Thrown when the PDF parser cannot open or process a PDF document.
 */
public class PDFLoadingException extends IOException {

    public PDFLoadingException(String message) {
        super(message);
    }

    public PDFLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

}
