package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaExtractionRequest;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse.IdeaRequest;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Coordinates idea extraction for a chapter by fetching sentences, invoking
 * LLM extraction, and persisting ideas, arguments, and sentence links.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final SentenceService sentenceService;
    private final IdeaRepository ideaRepo;
    private final UserRepository userRepo;

    private final BasicIdeaExtractionService extractionService;

    @Transactional
    public IdeaExtractionAiResponse extractIdeas(IdeaExtractionRequest request) {
        Long chapterId = request.chapterId();
        Long userId = request.userId();
        User user = userRepo.getReferenceById(userId);
        log.info("Fetching sentences for chapter {}...", chapterId);
        List<Sentence> sentences = sentenceService.getSentencesByChapterIdAndUserId(chapterId, userId);
        log.info("Fetched {} sentences for chapter {}", sentences.size(), request);

        if (sentences.isEmpty()) {
            throw new EntityNotFoundException(
                    "No sentences found for chapter %s and user %s".formatted(chapterId, userId));
        }

        // Index sentences by ID for O(1) lookup during idea–sentence linking
        Map<Long, Sentence> sentencesByIds = sentences.stream()
                .collect(Collectors.toMap(Sentence::getId, sentence -> sentence));

        log.info("Calling LLM for idea extraction...");
        IdeaExtractionAiResponse extractionResponse = extractionService.getIdeas(sentences);
        log.info("LLM found {} ideas in chapter {}", extractionResponse.ideaContainers().size(), request);

        log.info("Saving {} ideas and sentences they belong too...", extractionResponse.ideaContainers().size());
        extractionResponse.ideaContainers().forEach(saveIdeaContainer(sentencesByIds, user));
        log.info("Saved {} ideas from chapter {}", extractionResponse.ideaContainers().size(), request);

        return extractionResponse;
    }

    private Consumer<? super IdeaRequest> saveIdeaContainer(Map<Long, Sentence> sentencesByIds, User user) {
        return ideaContainer -> {
            Idea idea = new Idea();
            idea.setTitle(ideaContainer.ideaTitle());
            idea.setUser(user);

            List<IdeaArgument> ideaArguments = ideaContainer.arguments().stream().map(argText -> {
                IdeaArgument argument = new IdeaArgument();
                argument.setIdea(idea);
                argument.setText(argText);
                argument.setUser(user);
                return argument;
            }).toList();

            idea.setArguments(ideaArguments);

            // create a link between idea and sentences it is contained in
            List<IdeaSentence> ideaSentences = ideaContainer.ideaSentencesIds().stream().map(id -> {
                IdeaSentence ideaSentence = new IdeaSentence();
                ideaSentence.setIdea(idea);
                ideaSentence.setSentence(sentencesByIds.getOrDefault(id, null));
                return ideaSentence.getSentence() == null ? null : ideaSentence;
            }).filter(Objects::nonNull).toList(); // LLM may return sentence IDs that don't exist in the database —
                                                  // null entries are filtered out

            idea.setSentences(ideaSentences);

            // Cascade-save idea with its arguments and sentence links
            ideaRepo.save(idea);
        };
    }

}
