package com.orio.book_processing.services;

import java.io.IOException;

public class FileContentException extends IOException {

    public FileContentException(String arg0) {
        super(arg0);
    }

    public FileContentException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }
}
