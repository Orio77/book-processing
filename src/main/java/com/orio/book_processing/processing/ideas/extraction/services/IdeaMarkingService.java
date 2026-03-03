package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaMarkingService {

    private final ChatModel chatModel;

    private final SentenceService sentenceService;
    private final IdeaSentenceRepository ideaSentenceRepo;

    private static final int SENTENCE_BATCH = 5;
    private static final String IDEA_SENTENCE_FILTER_PROMPT = """
                Tell me whether the following idea is contained within any of the following sentences. if it is, add it's id to the output list. Here you have the sentences it is already contained in for reference (it may be empty initially):

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

    public void markIdeas(Long chapterId, List<Idea> ideas) {
        log.info("Marking {} ideas for chapter {}...", ideas.size(), chapterId);
        List<Sentence> ideaSentenceAccumulator = new ArrayList<>();
        List<Sentence> chapterSentences = sentenceService.getSentencesByChapterId(chapterId);
        // trim for testing
        chapterSentences = chapterSentences.subList(0, SENTENCE_BATCH * 2);
        ListOutputConverter lstOutConv = new ListOutputConverter();

        // For each idea
        for (Idea idea : ideas) {
            // For each sentence, in batches
            for (int i = 0; i < chapterSentences.size(); i = i + SENTENCE_BATCH) {
                List<Sentence> sentencesToJudge = getSentencesToJudge(chapterSentences, i);
                List<Long> ids = getIds(ideaSentenceAccumulator, lstOutConv, idea, sentencesToJudge);

                ideaSentenceAccumulator
                        .addAll(sentencesToJudge.stream().filter(sntnc -> ids.contains(sntnc.getId())).toList());
            }
            createAndSaveIdeaSentences(ideaSentenceAccumulator, idea);
        }
    }

    private void createAndSaveIdeaSentences(List<Sentence> ideaSentenceAccumulator, Idea idea) {
        List<IdeaSentence> ideaSentences = ideaSentenceAccumulator.stream()
                .map(sntnc -> new IdeaSentence(idea, sntnc)).toList();
        ideaSentenceRepo.saveAll(ideaSentences);
        ideaSentenceAccumulator.clear();
        log.info("Marked {} ideas successfully", ideaSentenceAccumulator.size());
    }

    private List<Sentence> getSentencesToJudge(List<Sentence> chapterSentences, int i) {
        return chapterSentences.subList(i,
                i + SENTENCE_BATCH >= chapterSentences.size() ? chapterSentences.size() : i + SENTENCE_BATCH);
    }

    private List<Long> getIds(List<Sentence> ideaSentenceAccumulator, ListOutputConverter lstOutConv, Idea idea,
            List<Sentence> sentencesToJudge) {
        ChatResponse response = chatModel
                .call(new Prompt(IDEA_SENTENCE_FILTER_PROMPT.formatted(idea.getTitle(), ideaSentenceAccumulator,
                        sentencesToJudge, lstOutConv.getFormat())));
        log.debug(response.getResult().getOutput().getText());

        String responseText = response.getResult().getOutput().getText();
        return getIds(responseText);
    }

    private List<Long> getIds(String responseText) {
        String[] idStrings = responseText.replace('[', ' ').replace(']', ' ').trim().split(", ");
        return Arrays.stream(idStrings).filter(s -> !s.isBlank()).map(Long::decode).toList();

    }
}
