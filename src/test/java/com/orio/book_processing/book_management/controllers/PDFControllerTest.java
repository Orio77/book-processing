package com.orio.book_processing.book_management.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.book_management.services.pdf.PDFService;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = PDFController.class)
class PDFControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PDFService pdfService;
    @MockitoBean
    private ChapterService chapterService;
    @MockitoBean
    private SentenceService sentenceService;
    @MockitoBean
    private JobDispatcher jobDispatcher;

    @Test
    void uploadPdf_enqueuesJob() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.PDF_UPLOAD), any())).thenReturn(99L);
        byte[] pdf = new byte[] { 1 };
        String rangesJson = objectMapper.writeValueAsString(List.of(new PageRange(1, 1)));
        mockMvc.perform(multipart("/api/pdf/upload")
                .file(new MockMultipartFile("file", "a.pdf", "application/pdf", pdf))
                .file(new MockMultipartFile("chapterPageRanges", "", MediaType.APPLICATION_JSON_VALUE,
                        rangesJson.getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isAccepted())
                .andExpect(content().string("99"));
    }

    @Test
    void uploadPdf_ioException_returns500() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[] {}) {
            @Override
            public byte[] getBytes() throws java.io.IOException {
                throw new java.io.IOException("fail");
            }
        };
        String rangesJson = objectMapper.writeValueAsString(List.of(new PageRange(1, 1)));
        mockMvc.perform(multipart("/api/pdf/upload")
                .file(file)
                .file(new MockMultipartFile("chapterPageRanges", "", MediaType.APPLICATION_JSON_VALUE,
                        rangesJson.getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void uploadPdf_jsonError_returns400() throws Exception {
        when(jobDispatcher.enqueue(eq(JobType.PDF_UPLOAD), any())).thenThrow(JsonProcessingException.class);
        String rangesJson = objectMapper.writeValueAsString(List.of(new PageRange(1, 1)));
        mockMvc.perform(multipart("/api/pdf/upload")
                .file(new MockMultipartFile("file", "a.pdf", "application/pdf", new byte[] { 1 }))
                .file(new MockMultipartFile("chapterPageRanges", "", MediaType.APPLICATION_JSON_VALUE,
                        rangesJson.getBytes(StandardCharsets.UTF_8))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPdf_found() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(1L);
        pdf.setTitle("t");
        pdf.setTotalPages(3);
        pdf.setCreatedAt(LocalDateTime.parse("2024-01-15T10:00:00"));
        when(pdfService.getPdf(1L)).thenReturn(pdf);
        mockMvc.perform(get("/api/pdf/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("t"))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getPdf_missing_returns404() throws Exception {
        when(pdfService.getPdf(1L)).thenThrow(new EntityNotFoundException());
        mockMvc.perform(get("/api/pdf/get/1")).andExpect(status().isNotFound());
    }

    @Test
    void getAllPdfs_empty_returns204() throws Exception {
        when(pdfService.getAllPdfs()).thenReturn(List.of());
        mockMvc.perform(get("/api/pdf/get/all")).andExpect(status().isNoContent());
    }

    @Test
    void getAllPdfs_returnsList() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(2L);
        pdf.setTitle("x");
        pdf.setTotalPages(1);
        pdf.setCreatedAt(LocalDateTime.parse("2024-02-01T12:00:00"));
        when(pdfService.getAllPdfs()).thenReturn(List.of(pdf));
        mockMvc.perform(get("/api/pdf/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void deletePdf_deleted_returnsOk() throws Exception {
        when(pdfService.deletePDF(3L)).thenReturn(true);
        mockMvc.perform(delete("/api/pdf/delete/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deletePdf_missing_returns404() throws Exception {
        when(pdfService.deletePDF(3L)).thenReturn(false);
        mockMvc.perform(delete("/api/pdf/delete/3")).andExpect(status().isNotFound());
    }

    @Test
    void getChapter_found() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(9L);
        Chapter ch = new Chapter();
        ch.setId(5L);
        ch.setTitle("ct");
        ch.setStartPage(1);
        ch.setEndPage(2);
        ch.setPdf(pdf);
        when(chapterService.getChapter(5L)).thenReturn(ch);
        mockMvc.perform(get("/api/pdf/chapter/get/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pdfId").value(9));
    }

    @Test
    void getChapter_missing_returns404() throws Exception {
        when(chapterService.getChapter(5L)).thenThrow(new EntityNotFoundException());
        mockMvc.perform(get("/api/pdf/chapter/get/5")).andExpect(status().isNotFound());
    }

    @Test
    void getAllChapters_empty_returns204() throws Exception {
        when(chapterService.getAllChapters(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/pdf/chapter/get/all/1")).andExpect(status().isNoContent());
    }

    @Test
    void getAllChapters_returnsData() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(1L);
        Chapter ch = new Chapter();
        ch.setId(8L);
        ch.setTitle(null);
        ch.setStartPage(1);
        ch.setEndPage(1);
        ch.setPdf(pdf);
        when(chapterService.getAllChapters(1L)).thenReturn(List.of(ch));
        mockMvc.perform(get("/api/pdf/chapter/get/all/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(8));
    }

    @Test
    void getSentencesInRange_empty_returns204() throws Exception {
        when(sentenceService.getSentencesInRange(any(PageRange.class), eq(1L))).thenReturn(List.of());
        mockMvc.perform(get("/api/pdf/sentence/get/1").param("startPage", "1").param("endPage", "2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSentencesInRange_returnsData() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(1L);
        Chapter ch = new Chapter();
        ch.setId(2L);
        Sentence s = new Sentence();
        s.setId(3L);
        s.setContent("hello");
        s.setSentenceIndex(0);
        s.setPdf(pdf);
        s.setChapter(ch);
        when(sentenceService.getSentencesInRange(any(PageRange.class), eq(1L))).thenReturn(List.of(s));
        mockMvc.perform(get("/api/pdf/sentence/get/1").param("startPage", "1").param("endPage", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("hello"));
    }

    @Test
    void getSentencesInRanges_empty_returns204() throws Exception {
        when(sentenceService.getSentencesInRanges(any(), eq(1L))).thenReturn(List.of());
        mockMvc.perform(post("/api/pdf/sentence/get/ranges/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(new PageRange(1, 2)))))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSentencesInRanges_returnsNested() throws Exception {
        PDF pdf = new PDF();
        pdf.setId(1L);
        Chapter ch = new Chapter();
        ch.setId(2L);
        Sentence s = new Sentence();
        s.setId(3L);
        s.setContent("x");
        s.setSentenceIndex(0);
        s.setPdf(pdf);
        s.setChapter(ch);
        when(sentenceService.getSentencesInRanges(any(), eq(1L))).thenReturn(List.of(List.of(s)));
        mockMvc.perform(post("/api/pdf/sentence/get/ranges/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(new PageRange(1, 1)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0][0].content").value("x"));
    }
}
