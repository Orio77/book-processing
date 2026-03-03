package com.orio.book_processing.processing.ideas.extraction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;

public interface IdeaRepository extends JpaRepository<Idea, Long> {

}
