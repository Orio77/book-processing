package com.orio.book_processing.processing.ideas.extraction.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;

public interface IdeaSentenceRepository extends JpaRepository<IdeaSentence, IdeaSentence.IdeaSentenceId> {

}
