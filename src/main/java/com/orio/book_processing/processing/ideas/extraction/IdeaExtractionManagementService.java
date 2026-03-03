package com.orio.book_processing.processing.ideas.extraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.book_management.services.sentence.SentenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final ChapterService chapterService;
    private final SentenceService sentenceService;
    private final ChatModel chatModel;
    private final IdeaRepository ideaRepo;
    private final IdeaSentenceRepository ideaSentenceRepo;

    private static final String EXTRACTION_PROMPT = """
                Extract core ideas from the given text.

                TEXT:
                \"""
                %s
                \"""

                Respond in the following format:
                %s
            """;

    public static final String IDEA_SENTENCE_FILTER_PROMPT = """
                Tell me whether the following idea is contained within any of the following sentences. if it is, add it's id to th output list. Here you have the sentences it is already contained in for reference (it may be empty initially):

                Idea:
                \"""
                %s
                \""";

                Idea_Sentences:
                \"""
                %s
                \""";

                New_Sentences:
                \"""
                %s
                \""";

                Response_Format:
                \"""
                %s
                \""";


            """;

    @Transactional
    public List<Idea> extractIdeas(Long chapterId) throws JsonParseException {

        String chapterText = getChapterText(chapterId);

        List<Idea> ideas = getIdeas(chapterText, chapterId).stream().map((Idea idea) -> {
            idea.setId(null);
            return idea;
        }).toList();

        ideaRepo.saveAll(ideas);

        log.info("Marking {} ideas for chapter {}...", ideas.size(), chapterId);
        List<Sentence> ideaSentenceAccumulator = new ArrayList<>();
        List<Sentence> chapterSentences = sentenceService.getSentencesInRange(new PageRange(0, 5), chapterId);
        final int sentenceBatch = 5;

        ListOutputConverter lstOutConv = new ListOutputConverter();

        for (Idea idea : ideas) {
            for (int i = 0; i < chapterSentences.size(); i = i + sentenceBatch) {
                List<Sentence> sentencesToJudge = chapterSentences.subList(i,
                        i + sentenceBatch >= chapterSentences.size() ? chapterSentences.size() : i + sentenceBatch);
                ChatResponse response = chatModel
                        .call(new Prompt(IDEA_SENTENCE_FILTER_PROMPT.formatted(idea.getTitle(), ideaSentenceAccumulator,
                                sentencesToJudge, lstOutConv.getFormat())));
                log.debug(response.getResult().getOutput().getText());

                String responseText = response.getResult().getOutput().getText();
                String[] idStrings = responseText.replace('[', ' ').replace(']', ' ').trim().split(", ");

                List<Long> ids = Arrays.stream(idStrings).filter(s -> !s.isBlank()).map(Long::decode).toList();

                ideaSentenceAccumulator
                        .addAll(sentencesToJudge.stream().filter(sntnc -> ids.contains(sntnc.getId())).toList());
            }
            List<IdeaSentence> ideaSentences = ideaSentenceAccumulator.stream()
                    .map(sntnc -> new IdeaSentence(idea, sntnc)).toList();
            ideaSentenceRepo.saveAll(ideaSentences);
            ideaSentenceAccumulator.clear();
        }
        log.info("Marked {} ideas for chapter {} successfully", ideaSentenceAccumulator.size(), chapterId);

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

    private List<Idea> getIdeas(String chapterText, Long chapterId) {
        BeanOutputConverter<List<Idea>> outConv = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Idea>>() {
                });

        log.info("Extracting ideas from chapter {}...", chapterId);
        // perform idea extraction here
        ChatResponse response = chatModel
                .call(new Prompt(EXTRACTION_PROMPT.formatted(chapterText, outConv.getFormat())));

        log.debug("Response received: \n\n{}\n\n", response.getResult().getOutput().getText());

        log.info("Converting the JSON response to objects...");
        List<Idea> ideas = outConv.convert(response.getResult().getOutput().getText());
        log.info("Created {} idea objects from the JSON response", ideas.size());
        return ideas;
    }
}
