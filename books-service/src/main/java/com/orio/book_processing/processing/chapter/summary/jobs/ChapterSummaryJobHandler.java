package com.orio.book_processing.processing.chapter.summary.jobs;

import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.chapter.summary.ChapterSummaryWorkflow;
import com.orio.book_processing.processing.chapter.summary.dtos.ChapterSummaryRequest;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.services.JobHandler;

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
