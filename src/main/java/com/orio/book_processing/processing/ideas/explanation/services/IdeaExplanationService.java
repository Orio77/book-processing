package com.orio.book_processing.processing.ideas.explanation.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.book_processing.processing.ideas.explanation.repositories.IdeaExplanationRepository;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExplanationService {

    private final IdeaExplanationRepository ideaExplanationRepo;
    private final IdeaRepository ideaRepo;
    private final IdeaExplanationGenerationService ideaExplanationGenerationService;

    public Optional<List<IdeaExplanation>> getExplanationsForIdea(Long ideaId) {
        log.info("Fetching explanations for idea {}...", ideaId);
        List<IdeaExplanation> ideaExplanations = ideaExplanationRepo.findAllByIdeaId(ideaId);
        log.info("Fetched {} explanations for idea {}.", ideaExplanations.size(), ideaId);
        return Optional.of(ideaExplanations);
    }

    public Optional<IdeaExplanation> getIdeaExplanation(Long explanationId) {
        log.info("Fetching explanation with id {}...", explanationId);
        Optional<IdeaExplanation> explanation = ideaExplanationRepo.findById(explanationId);
        if (explanation.isEmpty()) {
            log.warn("Explanation with id {} not found.", explanationId);
            return Optional.empty();
        }
        log.info("Fetched explanation with id {}.", explanationId);
        return explanation;
    }

    @Transactional
    public Optional<IdeaExplanation> update(Long explanationId, String newExplanationContent) {
        log.info("Updating explanation {}...", explanationId);
        if (newExplanationContent == null || newExplanationContent.isBlank()) {
            log.warn("Received explanation to update was missing data");
            return Optional.empty();
        }
        Optional<IdeaExplanation> oldExplanation = ideaExplanationRepo.findById(explanationId);
        if (oldExplanation.isEmpty()) {
            log.error("Explanation with id {} not found.", explanationId);
            return Optional.empty();
        }

        IdeaExplanation explanation = oldExplanation.get();
        explanation.setText(newExplanationContent);
        IdeaExplanation updatedExplanation = ideaExplanationRepo.saveAndFlush(explanation);
        log.info("Explanation updated successfully");
        return Optional.of(updatedExplanation);
    }

    @Transactional
    public boolean delete(Long explanationId) {
        log.info("Deleting explanation with id {}...", explanationId);
        try {
            if (ideaExplanationRepo.existsById(explanationId)) {
                ideaExplanationRepo.deleteById(explanationId);
                log.info("Explanation deleted successfully.");
                return true;
            } else {
                log.warn("Explanation with id {} not found for deletion.", explanationId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting explanation with id {}: {}", explanationId, e.getMessage());
            return false;
        }
    }

    @Transactional
    public Optional<IdeaExplanation> createExplanation(Long ideaId, String ideaContent) {
        if (ideaContent == null || ideaContent.isBlank()) {
            log.warn("Received explanation content is blank for idea {}.", ideaId);
            return Optional.empty();
        }

        Optional<Idea> idea = ideaRepo.findById(ideaId);
        if (idea.isEmpty()) {
            log.warn("Cannot create explanation. Idea with id {} not found.", ideaId);
            return Optional.empty();
        }

        Idea retrievedIdea = idea.get();
        String chapterText = retrievedIdea.getSentences().getFirst().getSentence().getChapter().getText();

        String ideaExplanationText = ideaExplanationGenerationService.generateIdeaExplanation(retrievedIdea.getTitle(),
                retrievedIdea.getArguments().stream().map(IdeaArgument::getText).toList(),
                chapterText);

        IdeaExplanation explanation = new IdeaExplanation();
        explanation.setText(ideaExplanationText);
        explanation.setIdea(retrievedIdea);

        log.debug("Explanation generated: {}", explanation.getText());

        IdeaExplanation savedExplanation = ideaExplanationRepo.save(explanation);
        log.info("Created explanation {} for idea {}.", savedExplanation.getId(), ideaId);
        return Optional.of(savedExplanation);
    }

}
