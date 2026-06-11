package com.orio.book_processing.chat.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.chat.dtos.PDFChatResponse;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

@WebMvcTest(controllers = ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ChatResponseService chatResponseService;
    @MockitoBean
    private JobDispatcher jobDispatcher;

    @Test
    void chat_enqueuesJob_returnsAccepted() throws Exception {
        var req = new PDFChatRequest(1L, "hi", List.of(new ChatContextSentenceDTO(2L, "x")));
        when(jobDispatcher.enqueue(eq(JobType.CHAT), any())).thenReturn(55L);

        mockMvc.perform(
                post("/api/pdf/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("55"));
    }

    @Test
    void chat_enqueueThrows_returns500() throws Exception {
        var req = new PDFChatRequest(1L, "hi", List.of());
        when(jobDispatcher.enqueue(eq(JobType.CHAT), any())).thenThrow(new RuntimeException("down"));

        mockMvc.perform(
                post("/api/pdf/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Chat request failed")));
    }

    @Test
    void getChatResponses_returnsList() throws Exception {
        when(chatResponseService.getChatResponsesForChapter(3L))
                .thenReturn(List.of(new PDFChatResponse("q", 9L, "body", List.of(1L, 2L))));

        mockMvc.perform(get("/api/pdf/chat/response/get/all/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatResponseId").value(9));
    }

    @Test
    void update_found_returnsOk() throws Exception {
        when(chatResponseService.update(1L, "new"))
                .thenReturn(java.util.Optional.of(new PDFChatResponse("q", 1L, "new", List.of(5L))));

        mockMvc.perform(put("/api/pdf/chat/response/edit/1").contentType(MediaType.TEXT_PLAIN).content("new"))
                .andExpect(status().isOk());
    }

    @Test
    void update_missing_returns404() throws Exception {
        when(chatResponseService.update(1L, "x")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/pdf/chat/response/edit/1").contentType(MediaType.TEXT_PLAIN).content("x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_found_returns204() throws Exception {
        when(chatResponseService.deleteChatResponse(2L)).thenReturn(true);

        mockMvc.perform(delete("/api/pdf/chat/response/delete/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missing_returns404() throws Exception {
        when(chatResponseService.deleteChatResponse(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/pdf/chat/response/delete/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatResponses_emptyChapter_returnsEmptyJsonArray() throws Exception {
        when(chatResponseService.getChatResponsesForChapter(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/pdf/chat/response/get/all/99"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
