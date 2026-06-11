package com.orio.book_processing.book_management.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.dtos.request.PdfUploadDTO;
import com.orio.book_processing.book_management.services.upload.PdfUploadService;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class PdfUploadJobHandlerTest {

    @Mock
    private PdfUploadService pdfUploadService;

    private PdfUploadJobHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PdfUploadJobHandler(pdfUploadService, new ObjectMapper());
    }

    @Test
    void supports_onlyPdfUpload() {
        assertTrue(handler.supports(JobType.PDF_UPLOAD));
        assertFalse(handler.supports(JobType.CHAT));
    }

    @Test
    void handle_deserializesAndDelegates() throws Exception {
        byte[] bytes = new byte[] { 1, 2, 3 };
        List<PageRange> ranges = List.of(new PageRange(1, 1));
        PdfUploadDTO dto = new PdfUploadDTO(bytes, "doc.pdf", "application/pdf", ranges);
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(dto);
        when(pdfUploadService.upload(any(MultipartFile.class), eq(ranges))).thenReturn(55L);

        Long result = handler.handle(json);

        assertEquals(55L, result);
        verify(pdfUploadService).upload(any(MultipartFile.class), eq(ranges));
    }
}
