package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaSentenceService {

    private final IdeaSentenceRepository ideaSentenceRepo;

    public List<Sentence> getSentencesByIdeaId(Long ideaId) {

        log.info("Fetching sentences for idea {}", ideaId);
        List<IdeaSentence> ideaSentences = ideaSentenceRepo.findAllByIdeaId(ideaId);
        log.info("Found {} sentences for idea {}", ideaSentences.size(), ideaId);

        return ideaSentences.stream().map(IdeaSentence::getSentence).toList();
    }

}
