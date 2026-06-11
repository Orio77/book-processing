package com.orio.book_processing.chat.jobs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.chat.ChatWorkflowService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.queue.models.Job.JobType;

@ExtendWith(MockitoExtension.class)
class ChatJobHandlerTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChatWorkflowService chatWorkflowService;

    @InjectMocks
    private ChatJobHandler handler;

    @Test
    void supports_onlyChatType() {
        assertTrue(handler.supports(JobType.CHAT));
        assertFalse(handler.supports(JobType.PDF_UPLOAD));
    }

    @Test
    void handle_parsesPayloadAndRunsWorkflow() throws Exception {
        PDFChatRequest req = new PDFChatRequest(4L, "q", List.of(new ChatContextSentenceDTO(10L, "c")));
        when(objectMapper.readValue(eq("{}"), eq(PDFChatRequest.class))).thenReturn(req);
        when(chatWorkflowService.chat(req.context(), req.query(), req.chapterId())).thenReturn(77L);

        assertEquals(77L, handler.handle("{}"));

        verify(chatWorkflowService).chat(req.context(), "q", 4L);
    }

    @Test
    void handle_invalidPayload_propagatesDeserializationFailure() throws Exception {
        when(objectMapper.readValue(eq("not-json"), eq(PDFChatRequest.class)))
                .thenThrow(new JsonProcessingException("bad") {
                });

        assertThrows(JsonProcessingException.class, () -> handler.handle("not-json"));
    }
}
