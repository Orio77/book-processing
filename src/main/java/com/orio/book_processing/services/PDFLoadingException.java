package com.orio.book_processing.services;

import java.io.IOException;

public class PDFLoadingException extends IOException {

    public PDFLoadingException(String arg0) {
        super(arg0);
    }

    public PDFLoadingException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
