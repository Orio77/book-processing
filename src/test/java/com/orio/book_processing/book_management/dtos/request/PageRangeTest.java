package com.orio.book_processing.book_management.dtos.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PageRangeTest {

    @Test
    void validRange() {
        PageRange r = new PageRange(2, 5);
        assertEquals(2, r.startPage());
        assertEquals(5, r.endPage());
    }

    @Test
    void startAfterEnd_throws() {
        assertThrows(IllegalArgumentException.class, () -> new PageRange(5, 2));
    }
}
