package com.orio.book_processing.book_management.exceptions;

import java.io.IOException;

/**
 * Thrown when uploaded file bytes cannot be read or interpreted correctly.
 */
public class FileContentException extends IOException {

    public FileContentException(String message) {
        super(message);
    }

    public FileContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
