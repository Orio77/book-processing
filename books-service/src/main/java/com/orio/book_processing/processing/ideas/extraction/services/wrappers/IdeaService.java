package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaDTO;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.book_processing.processing.ideas.extraction.dtos.SentenceDTO;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaService {

    private final IdeaRepository ideaRepo;
    private final IdeaSentenceService ideaSentenceService;
    private final ChapterService chapterService;
    private final IdeaSentenceRepository ideaSentenceRepo;

    public IdeaWithSentences getIdea(Long ideaId, Long userId) throws EntityNotFoundException {
        log.info("Looking up idea {} for user {}", ideaId, userId);
        Idea idea = ideaRepo.findByIdAndUserId(ideaId, userId)
                .orElseThrow(() -> {
                    log.warn("No idea found with id {} for user {}", ideaId, userId);
                    return new EntityNotFoundException("No idea found with id " + ideaId);
                });
        List<Sentence> sentences = ideaSentenceService.getSentencesByIdeaId(ideaId);
        return new IdeaWithSentences(IdeaDTO.from(idea), sentences.stream().map(SentenceDTO::from).toList());
    }

    @Transactional
    public Optional<List<IdeaWithSentences>> getIdeasByChapter(Long chapterId, Long userId) {
        log.info("Fetching ideas for chapter {}...", chapterId);

        Chapter chapter = chapterService.getChapter(chapterId, userId).orElseThrow(() -> new EntityNotFoundException(
                "No chapter found with id " + chapterId));
        List<Sentence> chapterSentences = chapter.getSentences();
        List<Long> chapterSentenceIds = chapterSentences.stream().map(Sentence::getId).toList();

        // fetch idea-sentence links
        List<IdeaSentence> ideaSentences = ideaSentenceRepo.findAllBySentence_IdIn(chapterSentenceIds);
        // group idea-sentence links by idea (to get all sentences for an idea)
        Map<Long, List<IdeaSentence>> ideaSentencesByIdeaId = ideaSentences.stream()
                .collect(Collectors.groupingBy(ideaSentence -> ideaSentence.getIdea().getId()));

        // Map ideas to the sentences they are contained it
        List<IdeaWithSentences> ideasWithSentences = ideaSentencesByIdeaId.entrySet().stream().map(entry -> {
            List<IdeaSentence> ideasIdeaSentences = entry.getValue();
            Idea idea = ideasIdeaSentences.getFirst().getIdea();
            List<SentenceDTO> sentenceDtos = ideasIdeaSentences.stream().map(IdeaSentence::getSentence)
                    .map(SentenceDTO::from).toList();

            return new IdeaWithSentences(IdeaDTO.from(idea), sentenceDtos);
        }).toList();

        log.info("Found {} ideas.", ideasWithSentences.size());

        return Optional.of(ideasWithSentences);
    }

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
