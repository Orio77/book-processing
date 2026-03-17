package com.orio.book_processing.book_management.exceptions;

import java.io.IOException;

public class PDFLoadingException extends IOException {

    public PDFLoadingException(String message) {
        super(message);
    }

    public PDFLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

}
