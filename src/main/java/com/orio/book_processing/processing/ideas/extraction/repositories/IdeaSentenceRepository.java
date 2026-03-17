package com.orio.book_processing.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;

/**
 * Repository for idea-to-sentence relationship entities.
 */
@Repository
public interface IdeaSentenceRepository extends JpaRepository<IdeaSentence, IdeaSentence.IdeaSentenceId> {

    public List<IdeaSentence> findAllByIdeaId(Long ideaId);

    public List<IdeaSentence> findAllBySentence_IdIn(List<Long> sentenceIds);

}
