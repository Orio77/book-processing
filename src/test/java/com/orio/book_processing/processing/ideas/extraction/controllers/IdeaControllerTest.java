package com.orio.book_processing.processing.ideas.extraction.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaArgumentDTO;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaDTO;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.book_processing.processing.ideas.extraction.dtos.SentenceDTO;
import com.orio.book_processing.processing.ideas.extraction.services.wrappers.IdeaArgumentService;
import com.orio.book_processing.processing.ideas.extraction.services.wrappers.IdeaService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

@WebMvcTest(controllers = IdeaController.class)
class IdeaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdeaService ideaService;
    @MockitoBean
    private IdeaArgumentService ideaArgumentService;
    @MockitoBean
    private JobDispatcher jobDispatcher;

    @Test
    void extractIdeasByChapterId_enqueuesJob() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.IDEA_EXTRACTION), eq(5L))).thenReturn(99L);

        mockMvc.perform(post("/api/pdf/process/idea/extract").param("chapterId", "5"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("99"));
    }

    @Test
    void extractIdeasByChapterId_whenEnqueueFails_returns400() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.IDEA_EXTRACTION), eq(5L)))
                .thenThrow(new JsonProcessingException("bad") { });

        mockMvc.perform(post("/api/pdf/process/idea/extract").param("chapterId", "5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllIdeasByChapterId_whenPresent_returns200() throws Exception {
        IdeaWithSentences payload = new IdeaWithSentences(new IdeaDTO(1L, "t"), List.of());
        when(ideaService.getIdeasByChapter(3L)).thenReturn(Optional.of(List.of(payload)));

        mockMvc.perform(get("/api/pdf/process/idea/get/all/3"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllIdeasByChapterId_whenMissing_returns404() throws Exception {
        when(ideaService.getIdeasByChapter(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pdf/process/idea/get/all/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIdeaById_whenMissing_returns404() throws Exception {
        when(ideaService.getIdea(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pdf/process/idea/get/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getArgumentsForIdea_whenMissing_returns404() throws Exception {
        when(ideaArgumentService.getIdeaArgumentsForIdea(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pdf/process/idea/argument/get/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getArgumentsForIdea_whenPresent_returnsJson() throws Exception {
        when(ideaArgumentService.getIdeaArgumentsForIdea(2L))
                .thenReturn(Optional.of(List.of(new IdeaArgumentDTO(9L, "x"))));

        mockMvc.perform(get("/api/pdf/process/idea/argument/get/2"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteIdeaById_propagatesServiceOptional() throws Exception {
        when(ideaService.deleteIdea(4L)).thenReturn(Optional.of(true));

        mockMvc.perform(delete("/api/pdf/process/idea/delete/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
