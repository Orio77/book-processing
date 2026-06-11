package com.orio.book_processing.book_management.dtos.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;

class ResponseDtoMappingTest {

    @Test
    void pdfResponse_from() {
        PDF pdf = new PDF();
        pdf.setId(1L);
        pdf.setTitle("t");
        pdf.setTotalPages(4);
        pdf.setCreatedAt(LocalDateTime.parse("2025-06-01T08:00:00"));
        PdfResponse r = PdfResponse.from(pdf);
        assertEquals(1L, r.id());
        assertEquals("t", r.title());
        assertEquals(4, r.totalPages());
        assertEquals(pdf.getCreatedAt(), r.createdAt());
    }

    @Test
    void chapterResponse_from() {
        PDF pdf = new PDF();
        pdf.setId(9L);
        Chapter ch = new Chapter();
        ch.setId(3L);
        ch.setTitle("c");
        ch.setStartPage(1);
        ch.setEndPage(10);
        ch.setPdf(pdf);
        ChapterResponse r = ChapterResponse.from(ch);
        assertEquals(3L, r.id());
        assertEquals("c", r.title());
        assertEquals(1, r.startPage());
        assertEquals(10, r.endPage());
        assertEquals(9L, r.pdfId());
    }

    @Test
    void sentenceResponse_from() {
        PDF pdf = new PDF();
        pdf.setId(1L);
        Chapter ch = new Chapter();
        ch.setId(2L);
        Sentence s = new Sentence();
        s.setId(7L);
        s.setContent("abc");
        s.setSentenceIndex(3);
        s.setPdf(pdf);
        s.setChapter(ch);
        SentenceResponse r = SentenceResponse.from(s);
        assertEquals(7L, r.id());
        assertEquals("abc", r.content());
        assertEquals(3, r.sentenceIndex());
        assertEquals(1L, r.pdfId());
        assertEquals(2L, r.chapterId());
    }
}
