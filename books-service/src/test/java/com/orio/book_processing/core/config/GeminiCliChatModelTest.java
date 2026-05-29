package com.orio.book_processing.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;

import com.orio.book_processing.core.exceptions.LLMGenerationException;

class GeminiCliChatModelTest {

    @AfterEach
    void clearInterruptedFlag() {
        Thread.interrupted();
    }

    @Test
    void test_call_throwsIllegalArgumentException_whenPromptIsNull() {
        GeminiCliChatModel model = new GeminiCliChatModel();

        assertThrows(IllegalArgumentException.class, () -> model.call((Prompt) null));
    }

    @Test
    void test_call_throwsIllegalArgumentException_whenPromptMessageIsBlank() {
        GeminiCliChatModel model = new GeminiCliChatModel();
        Prompt blankPrompt = new Prompt("   ");

        assertThrows(IllegalArgumentException.class, () -> model.call(blankPrompt));
    }

    @Test
    void test_call_throwsLLMGenerationException_whenProcessCannotStart() throws IOException {
        ProcessBuilder processBuilder = mock(ProcessBuilder.class);
        when(processBuilder.start()).thenThrow(new IOException("cannot start"));

        GeminiCliChatModel model = modelUsing(processBuilder);
        Prompt prompt = new Prompt("Hello Gemini");

        LLMGenerationException ex = assertThrows(LLMGenerationException.class,
                () -> model.call(prompt));

        assertTrue(ex.getMessage().contains("Failed to execute Gemini CLI process"));
    }

    @Test
    void test_call_throwsLLMGenerationException_whenProcessTimesOut() throws Exception {
        ProcessBuilder processBuilder = mock(ProcessBuilder.class);
        Process process = mock(Process.class);
        when(processBuilder.start()).thenReturn(process);
        when(process.getInputStream())
                .thenReturn(new ByteArrayInputStream("still running".getBytes(StandardCharsets.UTF_8)));
        when(process.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(process.waitFor(anyLong(), any(TimeUnit.class))).thenReturn(false, false);

        GeminiCliChatModel model = modelUsing(processBuilder);
        Prompt prompt = new Prompt("Hello Gemini");

        LLMGenerationException ex = assertThrows(LLMGenerationException.class,
                () -> model.call(prompt));

        assertTrue(ex.getMessage().contains("timed out"));
        verify(process).destroy();
        verify(process).destroyForcibly();
    }

    @Test
    void test_call_throwsLLMGenerationException_andRestoresInterruptFlag_whenInterrupted() throws Exception {
        ProcessBuilder processBuilder = mock(ProcessBuilder.class);
        Process process = mock(Process.class);
        when(processBuilder.start()).thenReturn(process);
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        when(process.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(process.waitFor(anyLong(), any(TimeUnit.class))).thenThrow(new InterruptedException("interrupted"));

        GeminiCliChatModel model = modelUsing(processBuilder);
        Prompt prompt = new Prompt("Hello Gemini");

        LLMGenerationException ex = assertThrows(LLMGenerationException.class,
                () -> model.call(prompt));

        assertTrue(ex.getMessage().contains("interrupted"));
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void test_call_throwsLLMGenerationException_whenGeminiExitCodeIsNonZero() throws Exception {
        ProcessBuilder processBuilder = mock(ProcessBuilder.class);
        Process process = mock(Process.class);
        when(processBuilder.start()).thenReturn(process);
        when(process.getInputStream())
                .thenReturn(new ByteArrayInputStream("fatal error".getBytes(StandardCharsets.UTF_8)));
        when(process.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(process.waitFor(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(process.exitValue()).thenReturn(2);

        GeminiCliChatModel model = modelUsing(processBuilder);
        Prompt prompt = new Prompt("Hello Gemini");

        LLMGenerationException ex = assertThrows(LLMGenerationException.class,
                () -> model.call(prompt));

        assertTrue(ex.getMessage().contains("exit code 2"));
        assertTrue(ex.getMessage().contains("fatal error"));
    }

    @Test
    void test_call_returnsChatResponse_andStripsCliNoiseOnSuccess() throws Exception {
        ProcessBuilder processBuilder = mock(ProcessBuilder.class);
        Process process = mock(Process.class);
        ByteArrayOutputStream stdinCapture = new ByteArrayOutputStream();
        String cliOutput = "Loaded cached credentials.\n{\"answer\":\"ok\"}";

        when(processBuilder.start()).thenReturn(process);
        when(process.getInputStream()).thenReturn(new ByteArrayInputStream(cliOutput.getBytes(StandardCharsets.UTF_8)));
        when(process.getOutputStream()).thenReturn(stdinCapture);
        when(process.waitFor(anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(process.exitValue()).thenReturn(0);

        GeminiCliChatModel model = modelUsing(processBuilder);

        var response = model.call(new Prompt("Hello Gemini"));

        assertEquals("Hello Gemini", stdinCapture.toString(StandardCharsets.UTF_8));
        assertEquals("{\"answer\":\"ok\"}", response.getResult().getOutput().getText());
    }

    private GeminiCliChatModel modelUsing(ProcessBuilder processBuilder) {
        return new GeminiCliChatModel() {
            @Override
            protected ProcessBuilder createProcessBuilder(List<String> command) {
                return processBuilder;
            }
        };
    }
}
