package com.orio.book_processing.book_management.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class ChapterTest {

    @Test
    void getText_joinsSentenceContents() {
        Sentence s1 = new Sentence();
        s1.setContent("Hello ");
        Sentence s2 = new Sentence();
        s2.setContent("world.");
        Chapter chapter = new Chapter();
        chapter.setSentences(List.of(s1, s2));
        assertEquals("Hello world.", chapter.getText());
    }
}
