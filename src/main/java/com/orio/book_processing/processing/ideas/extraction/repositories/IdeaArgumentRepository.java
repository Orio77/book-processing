package com.orio.book_processing.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;

public interface IdeaArgumentRepository extends JpaRepository<IdeaArgument, Long> {

    public List<IdeaArgument> findAllByIdea_Id(Long ideaId);
}
