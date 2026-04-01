package com.orio.book_processing.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

/**
 * Repository for persisted extracted ideas.
 */
@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findDistinctBySentencesSentenceChapterId(Long chapterId);
}
