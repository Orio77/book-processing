package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdeaSentenceService {

    private final IdeaSentenceRepository ideaSentenceRepo;

    public List<Sentence> getSentencesByIdeaId(Long ideaId) {

        List<IdeaSentence> ideaSentences = ideaSentenceRepo.findAllByIdeaId(ideaId);

        return ideaSentences.stream().map(IdeaSentence::getSentence).toList();
    }

}
