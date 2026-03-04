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
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse.IdeaRequest;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final SentenceService sentenceService;
    private final IdeaRepository ideaRepo;

    private final IdeaExtractionService extractionService;

    @Transactional
    public IdeaExtractionAiResponse extractIdeas(Long chapterId) {
        List<Sentence> sentences = sentenceService.getSentencesByChapterId(chapterId);
        Map<Long, Sentence> sentencesByIds = sentences.stream()
                .collect(Collectors.toMap(Sentence::getId, sentence -> sentence));
        IdeaExtractionAiResponse extractionResponse = extractionService.getIdeas(sentences);

        extractionResponse.ideaContainers().forEach(saveIdeaContainer(sentencesByIds));

        return extractionResponse;
    }

    private Consumer<? super IdeaRequest> saveIdeaContainer(Map<Long, Sentence> sentencesByIds) {
        return ideaContainer -> {
            Idea idea = new Idea();
            idea.setTitle(ideaContainer.ideaTitle());

            List<IdeaArgument> ideaArguments = ideaContainer.arguments().stream().map(argText -> {
                IdeaArgument argument = new IdeaArgument();
                argument.setIdea(idea);
                argument.setText(argText);
                return argument;
            }).toList();

            idea.setArguments(ideaArguments);

            List<IdeaSentence> ideaSentences = ideaContainer.ideaSentencesIds().stream().map(id -> {
                IdeaSentence ideaSentence = new IdeaSentence();
                ideaSentence.setIdea(idea);
                ideaSentence.setSentence(sentencesByIds.getOrDefault(id, null));
                return ideaSentence.getSentence() == null ? null : ideaSentence;
            }).filter(Objects::nonNull).toList();

            idea.setSentences(ideaSentences);

            ideaRepo.save(idea);
        };
    }

}
