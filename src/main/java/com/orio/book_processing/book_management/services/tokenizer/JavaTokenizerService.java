package com.orio.book_processing.book_management.services.tokenizer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
        log.debug("Created {} sentences", sentences.size());
        return sentences;
    }

}
