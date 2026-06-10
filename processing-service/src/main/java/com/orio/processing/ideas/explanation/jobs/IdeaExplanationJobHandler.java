package com.orio.processing.ideas.explanation.jobs;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.processing.ideas.explanation.dtos.IdeaExplanationRequest;
import com.orio.processing.ideas.explanation.exceptions.IdeaExplanationGenerationException;
import com.orio.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.processing.ideas.explanation.services.IdeaExplanationService;
import com.orio.processing.queue.JobType;
import com.orio.processing.queue.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExplanationJobHandler implements JobHandler {

    private final ObjectMapper objectMapper;
    private final IdeaExplanationService ideaExplanationService;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.IDEA_EXPLANATION;
    }

    @Override
    public Long handle(String payload) throws Exception {

        IdeaExplanationRequest ideaExplanationRequest = objectMapper.readValue(payload, IdeaExplanationRequest.class);
        Optional<IdeaExplanation> maybeExplanation = ideaExplanationService.createExplanation(
                ideaExplanationRequest.ideaId(),
                ideaExplanationRequest.ideaContent(), ideaExplanationRequest.userId());

        return maybeExplanation.map(IdeaExplanation::getId)
                .orElseThrow(() -> new IdeaExplanationGenerationException("Generated idea explanation was null"));
    }

}
