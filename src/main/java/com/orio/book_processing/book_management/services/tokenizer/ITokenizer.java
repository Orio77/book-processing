package com.orio.book_processing.book_management.services.tokenizer;

import java.util.List;

public interface ITokenizer {

    List<String> tokenize(String text);
}
