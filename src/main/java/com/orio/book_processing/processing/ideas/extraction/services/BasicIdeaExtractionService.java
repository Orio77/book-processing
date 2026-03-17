package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class BasicIdeaExtractionService implements IdeaExtractionService {

    private final ChatModel chatModel;

    private static final String EXTRACTION_PROMPT = """
                Extract the core ideas from the following text.

                Work through the text ONE IDEA AT A TIME:
                  1. Identify an idea.
                  2. Write the supporting arguments for THAT idea.
                  3. Provide ids of sentences THAT idea is contained in
                  4. Then move on to the next idea.

                  Avoid using quotation marks inside field values. Rephrase titles and arguments that would require them.

                The text sentences are formatted as {sentenceId: "sentence content"}.
                Use the sentenceId values as-is — do not invent or modify IDs.

                TEXT:
                \"""
                %s
                \"""

                Respond in the following format:
                %s
            """;

    /**
     * Extracts ideas from a list of sentences by calling an AI model and converting
     * its JSON response
     * into an {@link IdeaExtractionAiResponse} instance.
     *
     * @param sentences the sentences to extract ideas from
     * @return the extracted ideas wrapped in an {@link IdeaExtractionAiResponse}
     */
    @Override
    public IdeaExtractionAiResponse getIdeas(List<Sentence> sentences) {
        log.info("Extracting ideas from {} sentences...", sentences.size());
        String chapterText = sentences.toString();

        BeanOutputConverter<IdeaExtractionAiResponse> outputConverter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<IdeaExtractionAiResponse>() {
                });

        Prompt prompt = new Prompt(EXTRACTION_PROMPT.formatted(chapterText, outputConverter.getFormat()));
        log.debug("Calling model with extraction prompt: \n\n{}\n\n", prompt.toString());

        ChatResponse response = chatModel.call(prompt);
        log.debug("Response received: \n\n{}\n\n", response.getResult().getOutput().getText());

        log.info("Converting the JSON response to objects...");
        return outputConverter.convert(response.getResult().getOutput().getText());
    }
}
