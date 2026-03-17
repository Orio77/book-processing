package com.orio.book_processing.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Component
public class GeminiCliChatModel implements ChatModel {

    private static final long GEMINI_TIMEOUT_SECONDS = 120;

    @Override
    public ChatResponse call(Prompt prompt) {
        long startNanos = System.nanoTime();
        String userInput = prompt.getUserMessage().getText();
        if (userInput == null || userInput.isBlank()) {
            throw new IllegalArgumentException("Prompt user message is empty");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(buildCommand());

        try {
            Process process = processBuilder.start();
            log.info("gemini process started in {} ms", elapsedMs(startNanos));

            CompletableFuture<String> outputFuture = CompletableFuture
                    .supplyAsync(() -> drainStream(process.getInputStream()));

            process.getOutputStream().write(userInput.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();

            boolean finished = process.waitFor(GEMINI_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                destroyGracefully(process);
                throw new IllegalStateException(
                        "Gemini CLI timed out after " + GEMINI_TIMEOUT_SECONDS + " seconds");
            }
            log.info("gemini process finished in {} ms (exit {})", elapsedMs(startNanos), process.exitValue());

            String output = collectOutput(outputFuture);

            if (process.exitValue() != 0) {
                throw new IllegalStateException(
                        "Gemini CLI failed with exit code " + process.exitValue() + ": " + output);
            }

            String assistantText = stripCliNoise(output);
            log.info("gemini call completed in {} ms", elapsedMs(startNanos));

            return ChatResponse.builder()
                    .generations(List.of(new Generation(new AssistantMessage(assistantText))))
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Gemini CLI call was interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to execute Gemini CLI process", e);
        }
    }

    // --- process helpers ---

    private String drainStream(InputStream in) {
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Gemini CLI output", e);
        }
    }

    private String collectOutput(CompletableFuture<String> future) throws InterruptedException {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to read Gemini CLI output", e.getCause());
        } catch (TimeoutException e) {
            throw new IllegalStateException("Timed out collecting Gemini CLI output", e);
        }
    }

    private void destroyGracefully(Process process) throws InterruptedException {
        process.destroy();
        if (!process.waitFor(3, TimeUnit.SECONDS)) {
            process.destroyForcibly();
        }
    }

    /**
     * The text output format prefixes the response with status lines like
     * "Loaded cached credentials." — strip those and return only the model reply.
     */
    private String stripCliNoise(String output) {
        return output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1);
    }

    // --- command building ---

    private List<String> buildCommand() {
        String executable = resolveGeminiExecutable();
        log.info("using gemini executable: {}", executable);
        return List.of(executable, "-p", "", "--output-format", "text");
    }

    private String resolveGeminiExecutable() {
        String configured = System.getenv("GEMINI_CLI_PATH");
        if (configured != null && !configured.isBlank()) {
            return configured;
        }

        if (isWindows()) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                Path candidate = Path.of(appData, "npm", "gemini.cmd");
                if (Files.exists(candidate)) {
                    return candidate.toString();
                }
            }
        }

        return "gemini";
    }

    private boolean isWindows() {
        String os = System.getProperty("os.name");
        return os != null && os.toLowerCase().contains("win");
    }

    private long elapsedMs(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
