package com.orio.book_processing.processing.ideas.explanation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.orio.book_processing.processing.ideas.explanation.dtos.IdeaExplanationRequest;
import com.orio.book_processing.processing.ideas.explanation.jobs.IdeasExplanationRequest;
import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.book_processing.processing.ideas.explanation.services.IdeaExplanationService;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

@WebMvcTest(controllers = IdeaExplanationController.class)
class IdeaExplanationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IdeaExplanationService ideaExplanationService;
    @MockitoBean
    private JobDispatcher jobDispatcher;

    @Test
    void createExplanation_whenBlank_returns400() throws Exception {
        mockMvc.perform(post("/api/pdf/process/idea/1/explanation")
                .contentType(MediaType.TEXT_PLAIN)
                .content("   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createExplanation_enqueuesJob() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.IDEA_EXPLANATION), any(IdeaExplanationRequest.class))).thenReturn(55L);

        mockMvc.perform(post("/api/pdf/process/idea/2/explanation")
                .contentType(MediaType.TEXT_PLAIN)
                .content("non-empty body"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("55"));
    }

    @Test
    void createExplanation_whenEnqueueFails_returns400() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.IDEA_EXPLANATION), any(IdeaExplanationRequest.class)))
                .thenThrow(new JsonProcessingException("bad") { });

        mockMvc.perform(post("/api/pdf/process/idea/2/explanation")
                .contentType(MediaType.TEXT_PLAIN)
                .content("body"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createExplanations_enqueuesBatchJob() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.IDEAS_EXPLANATION), any(IdeasExplanationRequest.class))).thenReturn(66L);

        mockMvc.perform(post("/api/pdf/process/idea/9/explanations"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("66"));
    }

    @Test
    void getExplanationsForIdea_returnsMappedDtos() throws Exception {
        Idea idea = new Idea();
        idea.setId(7L);
        idea.setTitle("t");
        IdeaExplanation ex = new IdeaExplanation();
        ex.setId(100L);
        ex.setIdea(idea);
        ex.setText("txt");
        when(ideaExplanationService.getExplanationsForIdea(7L)).thenReturn(Optional.of(List.of(ex)));

        mockMvc.perform(get("/api/pdf/process/idea/7/explanations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].ideaId").value(7))
                .andExpect(jsonPath("$[0].content").value("txt"));
    }

    @Test
    void getIdeaExplanation_whenMissing_returns404() throws Exception {
        when(ideaExplanationService.getIdeaExplanation(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pdf/process/idea/explanations/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateIdeaExplanation_whenMissing_returns404() throws Exception {
        when(ideaExplanationService.update(eq(3L), eq("x"))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/pdf/process/idea/explanations/3")
                .contentType(MediaType.TEXT_PLAIN)
                .content("x"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteIdeaExplanation_whenDeleted_returns204() throws Exception {
        when(ideaExplanationService.delete(8L)).thenReturn(true);

        mockMvc.perform(delete("/api/pdf/process/idea/explanations/8"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteIdeaExplanation_whenNotFound_returns404() throws Exception {
        when(ideaExplanationService.delete(8L)).thenReturn(false);

        mockMvc.perform(delete("/api/pdf/process/idea/explanations/8"))
                .andExpect(status().isNotFound());
    }
}
