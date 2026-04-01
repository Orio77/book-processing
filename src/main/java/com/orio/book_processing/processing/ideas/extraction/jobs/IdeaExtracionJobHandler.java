package com.orio.book_processing.processing.ideas.extraction.jobs;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.processing.ideas.extraction.services.IdeaExtractionManagementService;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdeaExtracionJobHandler implements JobHandler {

    private final IdeaExtractionManagementService ideaExtractionManagementService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.IDEA_EXTRACTION;
    }

    @Override
    public Long handle(String payload) throws Exception {
        Long chapterId = objectMapper.readValue(payload, Long.class);
        ideaExtractionManagementService.extractIdeas(chapterId);
        return chapterId;
    }

}
