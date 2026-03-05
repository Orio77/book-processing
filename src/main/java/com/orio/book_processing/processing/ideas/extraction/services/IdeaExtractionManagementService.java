package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse.IdeaRequest;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final SentenceService sentenceService;
    private final IdeaRepository ideaRepo;

    private final IdeaExtractionService extractionService;

    @Transactional
    public IdeaExtractionAiResponse extractIdeas(Long chapterId) {
        log.info("Fetching sentences for chapter {}...", chapterId);
        List<Sentence> sentences = sentenceService.getSentencesByChapterId(chapterId);
        log.info("Fetched {} sentences for chapter {}", sentences.size(), chapterId);

        // group sentences by ids
        Map<Long, Sentence> sentencesByIds = sentences.stream()
                .collect(Collectors.toMap(Sentence::getId, sentence -> sentence));

        log.info("Calling LLM for idea estraction...");
        IdeaExtractionAiResponse extractionResponse = extractionService.getIdeas(sentences);
        log.info("LLM found {} ideas in chapter {}", extractionResponse.ideaContainers().size(), chapterId);

        log.info("Saving {} ideas and sentences they belong too...", extractionResponse.ideaContainers().size());
        extractionResponse.ideaContainers().forEach(saveIdeaContainer(sentencesByIds));
        log.info("Saved {} ideas from chapter {}", extractionResponse.ideaContainers().size(), chapterId);

        return extractionResponse;
    }

    private Consumer<? super IdeaRequest> saveIdeaContainer(Map<Long, Sentence> sentencesByIds) {
        return ideaContainer -> {
            // create idea
            Idea idea = new Idea();
            idea.setTitle(ideaContainer.ideaTitle());

            // create arguments
            List<IdeaArgument> ideaArguments = ideaContainer.arguments().stream().map(argText -> {
                IdeaArgument argument = new IdeaArgument();
                argument.setIdea(idea);
                argument.setText(argText);
                return argument;
            }).toList();

            // link arguments to the idea
            idea.setArguments(ideaArguments);

            // create a ling between idea and sentences it is contained in
            List<IdeaSentence> ideaSentences = ideaContainer.ideaSentencesIds().stream().map(id -> {
                IdeaSentence ideaSentence = new IdeaSentence();
                ideaSentence.setIdea(idea);
                ideaSentence.setSentence(sentencesByIds.getOrDefault(id, null));
                return ideaSentence.getSentence() == null ? null : ideaSentence;
            }).filter(Objects::nonNull).toList();

            idea.setSentences(ideaSentences);

            // cascade save
            ideaRepo.save(idea);
        };
    }

}
