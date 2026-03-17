package com.orio.book_processing.book_management.exceptions;

import java.io.IOException;

public class FileContentException extends IOException {

    public FileContentException(String message) {
        super(message);
    }

    public FileContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
