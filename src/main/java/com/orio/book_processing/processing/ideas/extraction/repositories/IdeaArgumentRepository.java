package com.orio.book_processing.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;

@Repository
public interface IdeaArgumentRepository extends JpaRepository<IdeaArgument, Long> {

    public List<IdeaArgument> findAllByIdea_Id(Long ideaId);
}
