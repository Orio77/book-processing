package com.orio.book_processing.chat.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.models.ChatResponse;
import com.orio.book_processing.chat.models.ChatResponseContext;
import com.orio.book_processing.chat.repositories.ChatResponseContextRepository;
import com.orio.book_processing.chat.repositories.ChatResponseRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ChatResponseServiceTest {

    @Mock
    private ChatResponseRepository chatResponseRepo;
    @Mock
    private ChatResponseContextRepository chatResponseContextRepo;
    @Mock
    private SentenceService sentenceService;

    @InjectMocks
    private ChatResponseService service;

    @Test
    void save_persistsResponseAndLinksSentences() {
        Sentence s1 = new Sentence();
        s1.setId(10L);
        when(sentenceService.getSentencesByIds(List.of(10L))).thenReturn(List.of(s1));

        ChatResponse saved = new ChatResponse();
        saved.setId(99L);
        when(chatResponseRepo.saveAndFlush(any(ChatResponse.class))).thenAnswer(inv -> {
            ChatResponse cr = inv.getArgument(0);
            cr.setId(99L);
            return cr;
        });

        List<ChatContextSentenceDTO> ctx = List.of(new ChatContextSentenceDTO(10L, "txt"));
        assertEquals(99L, service.save(5L, "q?", "body", ctx));

        ArgumentCaptor<ChatResponse> crCap = ArgumentCaptor.forClass(ChatResponse.class);
        verify(chatResponseRepo).saveAndFlush(crCap.capture());
        assertEquals(5L, crCap.getValue().getChapterId());
        assertEquals("q?", crCap.getValue().getQuery());
        assertEquals("body", crCap.getValue().getContent());

        verify(chatResponseContextRepo).saveAll(anyList());
    }

    @Test
    void save_emptyContext_persistsResponseAndSavesNoLinks() {
        when(sentenceService.getSentencesByIds(List.of())).thenReturn(List.of());

        ChatResponse saved = new ChatResponse();
        saved.setId(5L);
        when(chatResponseRepo.saveAndFlush(any(ChatResponse.class))).thenAnswer(inv -> {
            ChatResponse cr = inv.getArgument(0);
            cr.setId(5L);
            return cr;
        });

        assertEquals(5L, service.save(1L, "q", "body", List.of()));

        verify(chatResponseContextRepo).saveAll(List.of());
    }

    @Test
    void getChatResponsesForChapter_mapsSentenceIdsPerResponse() {
        ChatResponse r1 = new ChatResponse();
        r1.setId(1L);
        r1.setQuery("q");
        r1.setContent("c");
        when(chatResponseRepo.getByChapterId(3L)).thenReturn(List.of(r1));

        ChatResponseContext link = new ChatResponseContext();
        link.setChatResponse(r1);
        Sentence s = new Sentence();
        s.setId(7L);
        link.setSentence(s);
        when(chatResponseContextRepo.findByChatResponseIn(anyList())).thenReturn(List.of(link));

        var list = service.getChatResponsesForChapter(3L);
        assertEquals(1, list.size());
        assertEquals(List.of(7L), list.get(0).contextSentencesIds());
    }

    @Test
    void getChatResponsesForChapter_multipleResponses_mapsOnlyMatchingSentenceLinks() {
        ChatResponse r1 = new ChatResponse();
        r1.setId(1L);
        r1.setQuery("a");
        r1.setContent("ca");
        ChatResponse r2 = new ChatResponse();
        r2.setId(2L);
        r2.setQuery("b");
        r2.setContent("cb");
        when(chatResponseRepo.getByChapterId(10L)).thenReturn(List.of(r1, r2));

        ChatResponseContext link1 = new ChatResponseContext();
        link1.setChatResponse(r1);
        Sentence s10 = new Sentence();
        s10.setId(10L);
        link1.setSentence(s10);

        ChatResponseContext link2 = new ChatResponseContext();
        link2.setChatResponse(r2);
        Sentence s20 = new Sentence();
        s20.setId(20L);
        link2.setSentence(s20);

        ChatResponseContext orphan = new ChatResponseContext();
        orphan.setChatResponse(r1);
        Sentence s99 = new Sentence();
        s99.setId(99L);
        orphan.setSentence(s99);

        when(chatResponseContextRepo.findByChatResponseIn(anyList()))
                .thenReturn(List.of(link1, link2, orphan));

        var list = service.getChatResponsesForChapter(10L);
        assertEquals(2, list.size());
        assertEquals(List.of(10L, 99L), list.stream().filter(p -> p.chatResponseId().equals(1L)).findFirst().orElseThrow()
                .contextSentencesIds());
        assertEquals(List.of(20L), list.stream().filter(p -> p.chatResponseId().equals(2L)).findFirst().orElseThrow()
                .contextSentencesIds());
    }

    @Test
    void update_whenNoLinkedSentences_returnsEmpty() {
        ChatResponse cr = new ChatResponse();
        cr.setId(1L);
        cr.setQuery("q");
        when(chatResponseRepo.getReferenceById(1L)).thenReturn(cr);
        when(chatResponseRepo.saveAndFlush(cr)).thenReturn(cr);
        when(chatResponseContextRepo.findByChatResponse(cr)).thenReturn(List.of());

        assertTrue(service.update(1L, "new").isEmpty());
    }

    @Test
    void update_whenLinked_returnsDto() {
        ChatResponse cr = new ChatResponse();
        cr.setId(1L);
        cr.setQuery("q");
        when(chatResponseRepo.getReferenceById(1L)).thenReturn(cr);
        when(chatResponseRepo.saveAndFlush(cr)).thenReturn(cr);
        ChatResponseContext ctx = new ChatResponseContext();
        Sentence s = new Sentence();
        s.setId(8L);
        ctx.setSentence(s);
        when(chatResponseContextRepo.findByChatResponse(cr)).thenReturn(List.of(ctx));

        Optional<?> out = service.update(1L, "new");
        assertTrue(out.isPresent());
    }

    @Test
    void update_entityMissing_returnsEmpty() {
        when(chatResponseRepo.getReferenceById(2L)).thenThrow(new EntityNotFoundException());
        assertTrue(service.update(2L, "x").isEmpty());
    }

    @Test
    void deleteChatResponse_whenMissing_returnsFalse() {
        when(chatResponseRepo.existsById(9L)).thenReturn(false);
        assertFalse(service.deleteChatResponse(9L));
    }

    @Test
    void deleteChatResponse_whenPresent_deletesLinksThenEntity() {
        when(chatResponseRepo.existsById(9L)).thenReturn(true);
        when(chatResponseContextRepo.deleteByChatResponse_Id(9L)).thenReturn(2L);

        assertTrue(service.deleteChatResponse(9L));

        verify(chatResponseRepo).deleteById(9L);
    }
}
