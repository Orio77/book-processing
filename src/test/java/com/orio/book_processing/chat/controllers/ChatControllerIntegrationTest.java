package com.orio.book_processing.chat.controllers;

import static com.orio.book_processing.SampleTestDataFactory.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.orio.book_processing.IntegrationTestBase;
import com.orio.book_processing.auth.config.AuthConfig;
import com.orio.book_processing.auth.services.AuthUserDetailsService;
import com.orio.book_processing.chat.dtos.PDFChatRequest;
import com.orio.book_processing.chat.dtos.PDFChatResponse;
import com.orio.book_processing.chat.services.impl.ChatResponseService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobDispatcher;

@SpringBootTest(classes = ChatControllerIntegrationTest.TestConfig.class)
class ChatControllerIntegrationTest extends IntegrationTestBase {

    @Configuration
    @Import({ ChatController.class, AuthConfig.class })
    static class TestConfig {
    }

    @MockitoBean
    private ChatResponseService chatResponseService;

    @MockitoBean
    private JobDispatcher jobDispatcher;

    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    // =========================================================================
    // POST /api/pdf/chat tests
    // =========================================================================

    @Test
    void chat_ShouldReturnAcceptedAndJobId_WhenAuthorizedRequestIsValid() throws Exception {
        // Arrange
        Long userId = 123L;
        Long expectedJobId = 999L;
        PDFChatRequest requestBody = pdfChatRequest();

        // PDFChatRequest.from updates the request with the authenticated user's ID
        PDFChatRequest expectedUserRequest = pdfChatRequest(userId);

        when(jobDispatcher.enqueue(JobType.CHAT, expectedUserRequest, userId))
                .thenReturn(expectedJobId);

        // Act & Assert
        mockMvc.perform(post("/api/pdf/chat")
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(expectedJobId.toString()));

        verify(jobDispatcher, times(1)).enqueue(JobType.CHAT, expectedUserRequest, userId);
    }

    @Test
    void chat_ShouldReturnInternalServerError_WhenJobDispatcherThrowsException() throws Exception {
        // Arrange
        Long userId = 123L;
        PDFChatRequest requestBody = pdfChatRequest();
        PDFChatRequest expectedUserRequest = pdfChatRequest(userId);

        when(jobDispatcher.enqueue(JobType.CHAT, expectedUserRequest, userId))
                .thenThrow(new RuntimeException("Queue is full or processor crashed"));

        // Act & Assert
        mockMvc.perform(post("/api/pdf/chat")
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(content()
                        .string("Chat request failed: java.lang.RuntimeException: Queue is full or processor crashed"));

        verify(jobDispatcher, times(1)).enqueue(JobType.CHAT, expectedUserRequest, userId);
    }

    @Test
    void chat_ShouldReturnUnauthorized_WhenRequestNoToken() throws Exception {
        // Arrange
        PDFChatRequest requestBody = pdfChatRequestWithEmptyContext();

        // Act & Assert
        mockMvc.perform(post("/api/pdf/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // GET /api/pdf/chat/response/get/all/{chapterId} tests
    // =========================================================================

    @Test
    void getChatResponsesForChapter_ShouldReturnOkAndList_WhenAuthorized() throws Exception {
        // Arrange
        Long userId = 123L;
        Long chapterId = 10L;
        PDFChatResponse response1 = pdfChatResponse("Explain the topic", 456L, "Here is the explanation",
                List.of(1L));
        PDFChatResponse response2 = pdfChatResponse("Explain again", 457L, "Another explanation", List.of(1L, 2L));
        List<PDFChatResponse> expectedResponses = List.of(response1, response2);

        when(chatResponseService.getChatResponsesForChapter(chapterId, userId))
                .thenReturn(expectedResponses);

        // Act & Assert
        mockMvc.perform(get("/api/pdf/chat/response/get/all/{chapterId}", chapterId)
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].query").value("Explain the topic"))
                .andExpect(jsonPath("$[0].chatResponseId").value(456))
                .andExpect(jsonPath("$[0].chatResponse").value("Here is the explanation"))
                .andExpect(jsonPath("$[0].contextSentencesIds[0]").value(1))
                .andExpect(jsonPath("$[1].query").value("Explain again"))
                .andExpect(jsonPath("$[1].chatResponseId").value(457))
                .andExpect(jsonPath("$[1].chatResponse").value("Another explanation"));

        verify(chatResponseService, times(1)).getChatResponsesForChapter(chapterId, userId);
    }

    @Test
    void getChatResponsesForChapter_ShouldReturnUnauthorized_WhenRequestNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/pdf/chat/response/get/all/10"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // PUT /api/pdf/chat/response/edit/{chatResponseId} tests
    // =========================================================================

    @Test
    void updateChatResponse_ShouldReturnOkAndResponse_WhenResponseExistsAndAuthorized() throws Exception {
        // Arrange
        Long userId = 123L;
        Long chatResponseId = 456L;
        String newBody = "updated body text";
        PDFChatResponse updatedResponse = pdfChatResponse("Explain the topic", chatResponseId, newBody,
                List.of(1L));

        when(chatResponseService.update(chatResponseId, newBody, userId))
                .thenReturn(Optional.of(updatedResponse));

        // Act & Assert
        mockMvc.perform(put("/api/pdf/chat/response/edit/{chatResponseId}", chatResponseId)
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId)))
                .contentType(MediaType.TEXT_PLAIN) // ChatController accepts raw String as @RequestBody
                .content(newBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("Explain the topic"))
                .andExpect(jsonPath("$.chatResponseId").value(chatResponseId))
                .andExpect(jsonPath("$.chatResponse").value(newBody))
                .andExpect(jsonPath("$.contextSentencesIds[0]").value(1));

        verify(chatResponseService, times(1)).update(chatResponseId, newBody, userId);
    }

    @Test
    void updateChatResponse_ShouldReturnNotFound_WhenResponseDoesNotExist() throws Exception {
        // Arrange
        Long userId = 123L;
        Long chatResponseId = 456L;
        String newBody = "updated body text";

        when(chatResponseService.update(chatResponseId, newBody, userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/pdf/chat/response/edit/{chatResponseId}", chatResponseId)
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId)))
                .content(newBody))
                .andExpect(status().isNotFound());

        verify(chatResponseService, times(1)).update(chatResponseId, newBody, userId);
    }

    @Test
    void updateChatResponse_ShouldReturnUnauthorized_WhenRequestNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/pdf/chat/response/edit/456")
                .content("new body"))
                .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // DELETE /api/pdf/chat/response/delete/{chatResponseId} tests
    // =========================================================================

    @Test
    void deleteChatResponse_ShouldReturnNoContent_WhenSuccessfullyDeleted() throws Exception {
        // Arrange
        Long userId = 123L;
        Long chatResponseId = 456L;

        when(chatResponseService.deleteChatResponse(chatResponseId, userId))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/pdf/chat/response/delete/{chatResponseId}", chatResponseId)
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId))))
                .andExpect(status().isNoContent());

        verify(chatResponseService, times(1)).deleteChatResponse(chatResponseId, userId);
    }

    @Test
    void deleteChatResponse_ShouldReturnNotFound_WhenDeletionFailsResponseNotFound() throws Exception {
        // Arrange
        Long userId = 123L;
        Long chatResponseId = 456L;

        when(chatResponseService.deleteChatResponse(chatResponseId, userId))
                .thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/pdf/chat/response/delete/{chatResponseId}", chatResponseId)
                .with(jwt().jwt(jwtCustomizer -> jwtCustomizer.claim("uid", userId))))
                .andExpect(status().isNotFound());

        verify(chatResponseService, times(1)).deleteChatResponse(chatResponseId, userId);
    }

    @Test
    void deleteChatResponse_ShouldReturnUnauthorized_WhenRequestNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/pdf/chat/response/delete/456"))
                .andExpect(status().isUnauthorized());
    }
}
