package com.orio.book_processing.book_management.models;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SentenceTest {

    @Test
    void toString_containsIdAndContent() {
        Sentence s = new Sentence();
        s.setId(5L);
        s.setContent("hello");
        assertTrue(s.toString().contains("5"));
        assertTrue(s.toString().contains("hello"));
    }
}
