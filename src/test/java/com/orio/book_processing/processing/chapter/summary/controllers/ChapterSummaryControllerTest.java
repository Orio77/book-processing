package com.orio.book_processing.processing.chapter.summary.controllers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.summary.services.wrappers.ChapterSummaryService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = ChapterSummaryController.class)
class ChapterSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChapterSummaryService chapterSummaryService;
    @MockitoBean
    private JobDispatcher jobDispatcher;

    @Test
    void chapterSummary_enqueuesAccepted() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.CHAPTER_SUMMARY), eq(7L))).thenReturn(100L);
        mockMvc.perform(post("/api/pdf/process/chapter/summary").param("chapterId", "7"))
                .andExpect(status().isAccepted())
                .andExpect(content().string("100"));
    }

    @Test
    void chapterSummary_notFound_returns404() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.CHAPTER_SUMMARY), eq(7L))).thenThrow(new EntityNotFoundException());
        mockMvc.perform(post("/api/pdf/process/chapter/summary").param("chapterId", "7"))
                .andExpect(status().isNotFound());
    }

    @Test
    void chapterSummary_jsonProcessing_returns400() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.CHAPTER_SUMMARY), eq(7L)))
                .thenThrow(new JsonProcessingException("bad") {
                });
        mockMvc.perform(post("/api/pdf/process/chapter/summary").param("chapterId", "7"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSummaryByChapterId_found_returnsOk() throws Exception {
        ChapterSummary cs = new ChapterSummary();
        cs.setId(1L);
        Chapter ch = new Chapter();
        ch.setId(2L);
        cs.setChapter(ch);
        cs.setSummaryText("s");
        when(chapterSummaryService.findByChapterId(2L)).thenReturn(Optional.of(List.of(cs)));

        mockMvc.perform(get("/api/pdf/process/chapter/2/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].chapterId").value(2))
                .andExpect(jsonPath("$[0].summaryText").value("s"));
    }

    @Test
    void getSummaryByChapterId_missing_returns404() throws Exception {
        when(chapterSummaryService.findByChapterId(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/pdf/process/chapter/2/summary"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteChapterSummary_ok_returnsTrueBody() throws Exception {
        when(chapterSummaryService.deleteById(9L)).thenReturn(true);
        mockMvc.perform(delete("/api/pdf/process/chapter/summary/9"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getChapterSummary_found_returnsOkAndBody() throws Exception {
        ChapterSummary cs = new ChapterSummary();
        cs.setId(5L);
        Chapter ch = new Chapter();
        ch.setId(8L);
        cs.setChapter(ch);
        cs.setSummaryText("body");
        when(chapterSummaryService.getReferenceById(5L)).thenReturn(Optional.of(cs));

        mockMvc.perform(get("/api/pdf/process/chapter/summary/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.chapterId").value(8))
                .andExpect(jsonPath("$.summaryText").value("body"));
    }

    @Test
    void getChapterSummary_missing_returns404() throws Exception {
        when(chapterSummaryService.getReferenceById(5L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/pdf/process/chapter/summary/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteChapterSummary_missing_returns404() throws Exception {
        when(chapterSummaryService.deleteById(9L)).thenReturn(false);
        mockMvc.perform(delete("/api/pdf/process/chapter/summary/9"))
                .andExpect(status().isNotFound());
    }
}
