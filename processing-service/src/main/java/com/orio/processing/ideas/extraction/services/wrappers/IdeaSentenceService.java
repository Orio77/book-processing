package com.orio.processing.ideas.extraction.services.wrappers;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.processing.ideas.extraction.models.IdeaSentence;
import com.orio.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaSentenceService {

    private final IdeaSentenceRepository ideaSentenceRepo;

    public List<Long> getSentenceIdsByIdeaId(Long ideaId) {
        log.info("Fetching sentence links for idea {}", ideaId);
        List<IdeaSentence> ideaSentences = ideaSentenceRepo.findAllByIdeaId(ideaId);
        log.info("Found {} sentence links for idea {}", ideaSentences.size(), ideaId);

        return ideaSentences.stream().map(IdeaSentence::getSentenceId).toList();
    }

}
