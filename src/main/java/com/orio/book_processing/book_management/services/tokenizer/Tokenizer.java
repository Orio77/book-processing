package com.orio.book_processing.book_management.services.tokenizer;

import java.util.List;

/**
 * Contract for splitting chapter text into sentence-like units.
 */
public interface Tokenizer {

    List<String> tokenize(String text);
}
