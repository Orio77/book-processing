package com.orio.book_processing.processing.ideas.explanation.jobs;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.processing.ideas.explanation.dtos.IdeasExplanationRequest;
import com.orio.book_processing.processing.ideas.explanation.services.IdeaExplanationService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeasExplanationJobHandler implements JobHandler {

    private final ObjectMapper objectMapper;
    private final IdeaExplanationService ideaExplanationService;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.IDEAS_EXPLANATION;
    }

    @Override
    public Long handle(String payload) throws Exception {

        IdeasExplanationRequest ideasExplanationRequest = objectMapper.readValue(payload,
                IdeasExplanationRequest.class);
        ideaExplanationService.createExplanations(
                ideasExplanationRequest.chapterId(), ideasExplanationRequest.userId());

        return ideasExplanationRequest.chapterId();
    }

}
