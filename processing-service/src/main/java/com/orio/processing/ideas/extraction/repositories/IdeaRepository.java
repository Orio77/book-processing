package com.orio.processing.ideas.extraction.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.processing.ideas.extraction.models.Idea;

/**
 * Repository for persisted extracted ideas.
 */
@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByChapterIdAndUserId(Long chapterId, Long userId);

    Optional<Idea> findByIdAndUserId(Long ideaId, Long userId);

    void deleteByIdAndUserId(Long ideaId, Long userId);

    boolean existsByIdAndUserId(Long ideaId, Long userId);
}
