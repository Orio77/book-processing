package com.orio.book_processing.processing.ideas.extraction;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.orio.book_processing.book_management.services.chapter.ChapterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final ChapterService chapterService;
    private final ChatModel chatModel;

    private static final String EXTRACTION_PROMPT = """
                Extract core ideas from the given text.

                Respond in the following format:
                ```json
                [
                    {
                        "title": "idea1_title_here",
                        "supportingArguments": [
                            "idea_1_supporting_argument_1",
                            "idea_1_supporting_argument_2",
                                    ...
                        ]
                    },
                    {
                        "title": "idea2_title_here",
                        "supportingArguments": [
                            "idea_2_supporting_argument_1",
                            "idea_2_supporting_argument_2",
                                    ...
                        ]
                    },
                            ...
                ]
                ```

                Text:
                \"""
                %s
                \""";
            """;

    public List<Idea> extractIdeas(Long chapterId) throws JsonParseException {

        String chapterText = getChapterText(chapterId);

        ChatResponse response = getResponse(chapterId, chapterText);

        List<Idea> ideas = getIdeas(response);

        log.info("Marking {} ideas for chapter {}...", 1, chapterId);
        // perform idea marking here
        log.info("Marked {} ideas for chapter {} successfully", 1, chapterId);

        return ideas;

    }

    private String getChapterText(Long chapterId) {
        log.info("Parsing text for chapter {}...", chapterId);
        String chapterText = chapterService.getChapter(chapterId).getText();
        if (chapterText.length() > 5000) {
            log.info("Chapter text too long - {} chars, trimming...", chapterText.length());
            chapterText = chapterText.substring(0, 5000);
        }
        log.info("Chapter text parsed");
        return chapterText;
    }

    private ChatResponse getResponse(Long chapterId, String chapterText) {
        log.info("Extracting ideas from chapter {}...", chapterId);
        // perform idea extraction here
        ChatResponse response = chatModel.call(new Prompt(EXTRACTION_PROMPT.formatted(chapterText)));

        log.debug("Response received: \n\n{}\n\n", response.getResult().getOutput().getText());
        return response;
    }

    private List<Idea> getIdeas(ChatResponse response) {
        BeanOutputConverter<List<Idea>> outConv = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Idea>>() {
                });

        log.info("Converting the JSON response to objects...");
        List<Idea> ideas = outConv.convert(response.getResult().getOutput().getText());
        log.info("Created {} idea objects from the JSON response", ideas.size());
        return ideas;
    }
}
