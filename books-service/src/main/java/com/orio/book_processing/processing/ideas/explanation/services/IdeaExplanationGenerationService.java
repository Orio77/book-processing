package com.orio.book_processing.processing.ideas.explanation.services;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.ideas.explanation.models.LogicMapperExplanationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExplanationGenerationService {

    private final ChatModel chatModel;

    private static final String SYSTEM_PROMPT = "You are a Logic Mapper. Your task is to reconstruct the logical bridge between a raw text and a distilled concept.";

    private static final String IDEA_EXPLANATION_PROMPT = """
                ```

            ```
            Instructions: I have extracted a core concept from the text below. I need you to explain the logic the author used to arrive at this conclusion.
            Let's think step by step:
            1. First, identify where this concept appears or is alluded to in the text.
            2. Look for the "premises" — the sentences immediately preceding or following the concept that act as setup or justification.
            3. Look for "connectors" — words like "because," "therefore," "due to," or examples that illustrate the concept.
            4. Finally, write a paragraph starting with "The author argues this because..."
            Inputs: <concept> %s </concept>
            Output: Please provide your Step-by-Step reasoning first, followed by the Final Explanation.

            <chapter> %s </chapter>

            <format> %s </format>
            """;

    public String generateIdeaExplanation(String ideaTitle, List<String> ideaArguments, String chapterText) {
        log.info("Generating explanation response for idea {}...", ideaTitle);
        BeanOutputConverter<LogicMapperExplanationResponse> outputConverter = new BeanOutputConverter<>(
                LogicMapperExplanationResponse.class);
        Prompt prompt = new Prompt(List.of(new SystemMessage(SYSTEM_PROMPT), new UserMessage(
                IDEA_EXPLANATION_PROMPT.formatted(ideaTitle + "\n" + ideaArguments.toString(), chapterText,
                        outputConverter.getFormat()))));

        log.info("Calling {}...", chatModel.getDefaultOptions().getModel());
        log.debug("Calling {} with prompt: {}", chatModel.getDefaultOptions().getModel(), prompt.getContents());

        ChatResponse ideaExplanationResponse = chatModel.call(prompt);

        log.info("ChatResponse received from the LLM");

        log.info("Converting the LLM's output");

        @Nullable
        String responseText = ideaExplanationResponse.getResult().getOutput().getText();

        log.debug("Converting received response: {}", responseText);

        LogicMapperExplanationResponse convertedResponse = outputConverter.convert(responseText);

        log.info("Coversion successful, returning.");

        return convertedResponse.getReasoning() + "\n" + convertedResponse.getExplanation();
    }
}
