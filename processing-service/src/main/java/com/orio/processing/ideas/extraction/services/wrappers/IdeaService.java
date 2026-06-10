package com.orio.processing.ideas.extraction.services.wrappers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.processing.grpc.BooksClient;
import com.orio.processing.ideas.extraction.dtos.IdeaDTO;
import com.orio.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.processing.ideas.extraction.dtos.SentenceDTO;
import com.orio.processing.ideas.extraction.models.Idea;
import com.orio.processing.ideas.extraction.models.IdeaSentence;
import com.orio.processing.ideas.extraction.repositories.IdeaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaService {

    private final IdeaRepository ideaRepo;
    private final IdeaSentenceService ideaSentenceService;
    private final BooksClient booksClient;

    public IdeaWithSentences getIdea(Long ideaId, Long userId) throws EntityNotFoundException {
        log.info("Looking up idea {} for user {}", ideaId, userId);
        Idea idea = ideaRepo.findByIdAndUserId(ideaId, userId)
                .orElseThrow(() -> {
                    log.warn("No idea found with id {} for user {}", ideaId, userId);
                    return new EntityNotFoundException("No idea found with id " + ideaId);
                });

        List<Long> sentenceIds = ideaSentenceService.getSentenceIdsByIdeaId(ideaId);
        List<SentenceDTO> sentences = booksClient.getSentencesByIds(sentenceIds, userId).stream()
                .map(SentenceDTO::from).toList();

        return new IdeaWithSentences(IdeaDTO.from(idea), sentences);
    }

    @Transactional
    public Optional<List<IdeaWithSentences>> getIdeasByChapter(Long chapterId, Long userId) {
        log.info("Fetching ideas for chapter {}...", chapterId);

        List<Idea> ideas = ideaRepo.findAllByChapterIdAndUserId(chapterId, userId);

        List<IdeaWithSentences> ideasWithSentences = ideas.stream().map(idea -> {
            List<Long> sentenceIds = idea.getSentences().stream().map(IdeaSentence::getSentenceId).toList();
            List<SentenceDTO> sentences = sentenceIds.isEmpty() ? List.of()
                    : booksClient.getSentencesByIds(sentenceIds, userId).stream().map(SentenceDTO::from).toList();
            return new IdeaWithSentences(IdeaDTO.from(idea), sentences);
        }).toList();

        log.info("Found {} ideas.", ideasWithSentences.size());

        return Optional.of(ideasWithSentences);
    }

    @Transactional
    public Optional<Boolean> deleteIdea(Long ideaId, Long userId) {
        ideaRepo.deleteByIdAndUserId(ideaId, userId);

        log.info("Deleting idea {}", ideaId);
        Optional<Boolean> result = Optional.of(!ideaRepo.existsByIdAndUserId(ideaId, userId));

        result.ifPresentOrElse(
                res -> log.info("Idea {} deleted successfully", ideaId),
                () -> log.error("Failed to delete idea {}", ideaId));

        return result;
    }

}
