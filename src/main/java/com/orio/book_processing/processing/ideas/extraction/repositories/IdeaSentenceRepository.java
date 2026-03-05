package com.orio.book_processing.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;

public interface IdeaSentenceRepository extends JpaRepository<IdeaSentence, IdeaSentence.IdeaSentenceId> {

    public List<IdeaSentence> findAllByIdeaId(Long ideaId);

    public List<IdeaSentence> findAllBySentence_IdIn(List<Long> sentenceIds);

}
