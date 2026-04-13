package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaArgumentDTO;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaArgumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaArgumentService {

    private final IdeaArgumentRepository ideaArgumentRepo;

    public Optional<List<IdeaArgumentDTO>> getIdeaArgumentsForIdea(Long ideaId, Long userId) {
        log.info("Fetching arguments for idea {}...", ideaId);
        List<IdeaArgument> args = ideaArgumentRepo.findAllByIdea_IdAndUserId(ideaId, userId);
        log.info("Found {} arguments for idea {}", args.size(), ideaId);

        // convert arguments to DTOs
        return Optional.of(args.stream().map(arg -> new IdeaArgumentDTO(arg.getId(), arg.getText())).toList());
    }
}
