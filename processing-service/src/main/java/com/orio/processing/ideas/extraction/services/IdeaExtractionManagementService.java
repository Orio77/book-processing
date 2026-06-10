package com.orio.processing.ideas.extraction.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.processing.grpc.BooksClient;
import com.orio.processing.ideas.extraction.dtos.IdeaExtractionRequest;
import com.orio.processing.ideas.extraction.dtos.SentenceDTO;
import com.orio.processing.ideas.extraction.models.Idea;
import com.orio.processing.ideas.extraction.models.IdeaArgument;
import com.orio.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.processing.ideas.extraction.models.IdeaSentence;
import com.orio.processing.ideas.extraction.repositories.IdeaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Coordinates idea extraction for a chapter by fetching sentences from
 * books-service over gRPC, invoking LLM extraction, and persisting ideas,
 * arguments, and sentence-id links in the processing schema.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final BooksClient booksClient;
    private final IdeaRepository ideaRepo;
    private final BasicIdeaExtractionService extractionService;

    @Transactional
    public IdeaExtractionAiResponse extractIdeas(IdeaExtractionRequest request) {
        Long chapterId = request.chapterId();
        Long userId = request.userId();

        log.info("Fetching sentences for chapter {} over gRPC...", chapterId);
        List<SentenceDTO> sentences = booksClient.getChapterSentences(chapterId, userId).stream()
                .map(SentenceDTO::from)
                .toList();
        log.info("Fetched {} sentences for chapter {}", sentences.size(), chapterId);

        if (sentences.isEmpty()) {
            throw new EntityNotFoundException(
                    "No sentences found for chapter %s and user %s".formatted(chapterId, userId));
        }

        // LLM may return sentence IDs that don't exist — validate against this set
        Set<Long> knownSentenceIds = sentences.stream().map(SentenceDTO::id).collect(Collectors.toSet());

        log.info("Calling LLM for idea extraction...");
        IdeaExtractionAiResponse extractionResponse = extractionService.getIdeas(sentences);
        log.info("LLM found {} ideas in chapter {}", extractionResponse.ideaContainers().size(), chapterId);

        log.info("Saving {} ideas and their sentence links...", extractionResponse.ideaContainers().size());
        extractionResponse.ideaContainers().forEach(ideaContainer -> {
            Idea idea = new Idea();
            idea.setTitle(ideaContainer.ideaTitle());
            idea.setChapterId(chapterId);
            idea.setUserId(userId);

            List<IdeaArgument> ideaArguments = ideaContainer.arguments().stream().map(argText -> {
                IdeaArgument argument = new IdeaArgument();
                argument.setIdea(idea);
                argument.setText(argText);
                argument.setUserId(userId);
                return argument;
            }).toList();
            idea.setArguments(ideaArguments);

            List<IdeaSentence> ideaSentences = ideaContainer.ideaSentencesIds().stream()
                    .filter(knownSentenceIds::contains)
                    .map(sentenceId -> new IdeaSentence(idea, sentenceId))
                    .toList();
            idea.setSentences(ideaSentences);

            ideaRepo.save(idea);
        });
        log.info("Saved {} ideas from chapter {}", extractionResponse.ideaContainers().size(), chapterId);

        return extractionResponse;
    }

}
