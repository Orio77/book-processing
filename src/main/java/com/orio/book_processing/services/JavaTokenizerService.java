package com.orio.book_processing.services;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class JavaTokenizerService implements ITokenizer {

    @Override
    public List<String> tokenize(String str) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(str);
        List<String> sentences = new ArrayList<>();

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(str.substring(start, end));
        }
        return sentences;
    }

}
