package com.orio.book_processing.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.services.ExplanationChatService;
import com.orio.book_processing.chat.services.QueryChatService;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;

@ExtendWith(MockitoExtension.class)
class ChatWorkflowServiceTest {

    @Mock
    private QueryChatService queryChatService;
    @Mock
    private ExplanationChatService explanationChatService;
    @Mock
    private ChapterService chapterService;
    @Mock
    private ChatResponseService chatResponseService;

    @InjectMocks
    private ChatWorkflowService workflow;

    private static Chapter chapterReturningText(String text) {
        Chapter chapter = mock(Chapter.class);
        when(chapter.getText()).thenReturn(text);
        return chapter;
    }

    @Test
    void chat_nonBlankQuery_usesQueryService_joinsContext_andPersists() throws LLMGenerationException {
        Chapter chapter = chapterReturningText("chapter-body");
        when(chapterService.getChapterEagerly(9L)).thenReturn(chapter);
        when(queryChatService.generateChatResponse("frag", "why?", "chapter-body")).thenReturn("answer");
        when(chatResponseService.save(eq(9L), eq("why?"), eq("answer"), any())).thenReturn(100L);

        List<ChatContextSentenceDTO> ctx = List.of(new ChatContextSentenceDTO(1L, "frag"));
        assertEquals(100L, workflow.chat(ctx, "why?", 9L));

        verify(explanationChatService, never()).generateChatResponse(any(), any());
    }

    @Test
    void chat_blankQuery_usesExplanationService() throws LLMGenerationException {
        Chapter chapter = chapterReturningText("body");
        when(chapterService.getChapterEagerly(2L)).thenReturn(chapter);
        when(explanationChatService.generateChatResponse("ab", "body")).thenReturn("expl");
        when(chatResponseService.save(eq(2L), eq("  "), eq("expl"), any())).thenReturn(3L);

        List<ChatContextSentenceDTO> ctx = List.of(new ChatContextSentenceDTO(1L, "a"),
                new ChatContextSentenceDTO(2L, "b"));
        assertEquals(3L, workflow.chat(ctx, "  ", 2L));

        verify(queryChatService, never()).generateChatResponse(any(), any(), any());
    }

    @Test
    void chat_nullQuery_usesExplanationService() throws LLMGenerationException {
        Chapter chapter = chapterReturningText("body");
        when(chapterService.getChapterEagerly(7L)).thenReturn(chapter);
        when(explanationChatService.generateChatResponse("ctx", "body")).thenReturn("expl");
        when(chatResponseService.save(eq(7L), eq(null), eq("expl"), any())).thenReturn(42L);

        List<ChatContextSentenceDTO> ctx = List.of(new ChatContextSentenceDTO(1L, "ctx"));
        assertEquals(42L, workflow.chat(ctx, null, 7L));

        verify(queryChatService, never()).generateChatResponse(any(), any(), any());
    }

    @Test
    void chat_emptyStringQuery_usesExplanationService() throws LLMGenerationException {
        Chapter chapter = chapterReturningText("ch");
        when(chapterService.getChapterEagerly(1L)).thenReturn(chapter);
        when(explanationChatService.generateChatResponse("", "ch")).thenReturn("e");
        when(chatResponseService.save(eq(1L), eq(""), eq("e"), any())).thenReturn(2L);

        assertEquals(2L, workflow.chat(List.of(), "", 1L));

        verify(queryChatService, never()).generateChatResponse(any(), any(), any());
    }

    @Test
    void chat_nonBlankQuery_joinsMultipleContextFragmentsInOrder() throws LLMGenerationException {
        Chapter chapter = chapterReturningText("t");
        when(chapterService.getChapterEagerly(1L)).thenReturn(chapter);
        when(queryChatService.generateChatResponse("abc", "q", "t")).thenReturn("r");
        when(chatResponseService.save(eq(1L), eq("q"), eq("r"), any())).thenReturn(9L);

        List<ChatContextSentenceDTO> ctx = List.of(
                new ChatContextSentenceDTO(1L, "a"),
                new ChatContextSentenceDTO(2L, "b"),
                new ChatContextSentenceDTO(3L, "c"));
        assertEquals(9L, workflow.chat(ctx, "q", 1L));
    }
}
