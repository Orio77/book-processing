package com.orio.processing.ideas.extraction.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.processing.ideas.extraction.models.IdeaArgument;

/**
 * Repository for arguments that belong to extracted ideas.
 */
@Repository
public interface IdeaArgumentRepository extends JpaRepository<IdeaArgument, Long> {

    public List<IdeaArgument> findAllByIdea_IdAndUserId(Long ideaId, Long userId);
}
