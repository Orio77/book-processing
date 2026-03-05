package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgumentDTO;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaArgumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdeaArgumentService {

    private final IdeaArgumentRepository ideaArgumentRepo;

    public Optional<List<IdeaArgumentDTO>> getIdeaArgumentsForIdea(Long ideaId) {
        List<IdeaArgument> args = ideaArgumentRepo.findAllByIdea_Id(ideaId);
        return Optional.of(args.stream().map(arg -> new IdeaArgumentDTO(arg.getId(), arg.getText())).toList());
    }
}
