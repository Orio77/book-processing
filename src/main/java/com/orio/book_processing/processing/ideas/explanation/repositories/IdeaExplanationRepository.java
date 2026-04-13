package com.orio.book_processing.processing.ideas.explanation.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;

@Repository
public interface IdeaExplanationRepository extends JpaRepository<IdeaExplanation, Long> {

    List<IdeaExplanation> findAllByIdeaIdAndUserId(Long ideaId, Long userId);

    Optional<IdeaExplanation> findByIdAndUserId(Long explanationId, Long userId);

    boolean existsByIdAndUserId(Long explanationId, Long userId);

    void deleteByIdAndUserId(Long explanationId, Long userId);

}
