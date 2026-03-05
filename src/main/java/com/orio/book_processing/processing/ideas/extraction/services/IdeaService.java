package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaDTO;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaWithSentences;
import com.orio.book_processing.processing.ideas.extraction.models.SentenceDTO;
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

    public Optional<IdeaWithSentences> getIdea(Long ideaId) {
        try {
            Idea idea = ideaRepo.getReferenceById(ideaId);
            List<Sentence> sentences = ideaSentenceService.getSentencesByIdeaId(ideaId);
            return Optional
                    .of(new IdeaWithSentences(IdeaDTO.from(idea), sentences.stream().map(SentenceDTO::from).toList()));
        } catch (EntityNotFoundException e) {
            log.warn("Idea with id {} not found", ideaId);
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<List<IdeaWithSentences>> getIdeasByChapter(Long chapterId) {

        Chapter chapter = chapterService.getChapter(chapterId);
        List<Sentence> chapterSentences = chapter.getSentences();
        List<Long> chapterSentenceIds = chapterSentences.stream().map(Sentence::getId).toList();
        List<IdeaSentence> ideaSentences = ideaSentenceRepo.findAllBySentence_IdIn(chapterSentenceIds);
        Map<Long, List<IdeaSentence>> ideaSentencesByIdeaId = ideaSentences.stream()
                .collect(Collectors.groupingBy(ideaSentence -> ideaSentence.getIdea().getId()));

        List<IdeaWithSentences> ideasWithSentences = ideaSentencesByIdeaId.entrySet().stream().map(entry -> {
            List<IdeaSentence> ideasIdeaSentences = entry.getValue();
            Idea idea = ideasIdeaSentences.getFirst().getIdea();
            List<SentenceDTO> sentenceDtos = ideasIdeaSentences.stream().map(IdeaSentence::getSentence)
                    .map(SentenceDTO::from).toList();

            return new IdeaWithSentences(IdeaDTO.from(idea), sentenceDtos);
        }).toList();

        return Optional.of(ideasWithSentences);
    }

    public Optional<Boolean> deleteIdea(Long ideaId) {
        ideaRepo.deleteById(ideaId);

        return Optional.of(!ideaRepo.existsById(ideaId));
    }

}
