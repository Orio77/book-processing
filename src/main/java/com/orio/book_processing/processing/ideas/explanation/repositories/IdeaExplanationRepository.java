package com.orio.book_processing.processing.ideas.explanation.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;

@Repository
public interface IdeaExplanationRepository extends JpaRepository<IdeaExplanation, Long> {

    List<IdeaExplanation> findAllByIdeaId(Long ideaId);

}
