package com.orio.processing.chapter.summary.jobs;

import org.springframework.stereotype.Service;

import com.orio.processing.chapter.summary.ChapterSummaryWorkflow;
import com.orio.processing.chapter.summary.dtos.ChapterSummaryRequest;
import com.orio.processing.queue.JobType;
import com.orio.processing.queue.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterSummaryJobHandler implements JobHandler {

    private final ChapterSummaryWorkflow workflow;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.CHAPTER_SUMMARY;
    }

    @Override
    public Long handle(String payload) throws Exception {
        ChapterSummaryRequest chapterSummaryRequest = objectMapper.readValue(payload, ChapterSummaryRequest.class);
        return workflow.generateChapterSummary(chapterSummaryRequest);
    }

}
