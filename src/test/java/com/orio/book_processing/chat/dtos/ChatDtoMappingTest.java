package com.orio.book_processing.chat.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.orio.book_processing.book_management.models.Sentence;

class ChatDtoMappingTest {

    @Test
    void chatContextSentenceDto_fromSentence_mapsIdAndContent() {
        Sentence sentence = new Sentence();
        sentence.setId(12L);
        sentence.setContent("fragment text");

        ChatContextSentenceDTO dto = ChatContextSentenceDTO.from(sentence);

        assertEquals(12L, dto.sentenceId());
        assertEquals("fragment text", dto.sentenceContent());
    }

    @Test
    void pdfChatRequest_fromSentences_buildsContextInOrder() {
        Sentence a = new Sentence();
        a.setId(1L);
        a.setContent("A");
        Sentence b = new Sentence();
        b.setId(2L);
        b.setContent("B");

        PDFChatRequest req = PDFChatRequest.from(9L, "why?", List.of(a, b));

        assertEquals(9L, req.chapterId());
        assertEquals("why?", req.query());
        assertEquals(2, req.context().size());
        assertEquals(1L, req.context().get(0).sentenceId());
        assertEquals("A", req.context().get(0).sentenceContent());
        assertEquals(2L, req.context().get(1).sentenceId());
        assertEquals("B", req.context().get(1).sentenceContent());
    }

    @Test
    void pdfChatRequest_fromEmptySentences_yieldsEmptyContext() {
        PDFChatRequest req = PDFChatRequest.from(1L, "q", List.of());
        assertTrue(req.context().isEmpty());
    }
}
