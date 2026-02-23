package com.orio.book_processing.services.tokenizer;

import java.util.List;

public interface ITokenizer {

    List<String> tokenize(String text);
}
