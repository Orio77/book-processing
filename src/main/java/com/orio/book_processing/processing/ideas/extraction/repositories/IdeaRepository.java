package com.orio.book_processing.processing.ideas.extraction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

}
