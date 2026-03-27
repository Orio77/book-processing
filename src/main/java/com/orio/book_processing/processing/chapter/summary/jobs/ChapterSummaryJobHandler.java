package com.orio.book_processing.processing.chapter.summary.jobs;

import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.Job.JobType;
import com.orio.book_processing.processing.chapter.summary.ChapterSummaryWorkflow;
import com.orio.book_processing.queue.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterSummaryJobHandler implements JobHandler {

    private final ChapterSummaryWorkflow workflow;

    @Override
    public boolean supports(JobType jobType) {
        return jobType == JobType.CHAPTER_SUMMARY;
    }

    @Override
    public Long handle(String payload) throws Exception {
        Long chapterId = Long.decode(payload);
        return workflow.generateChapterSummary(chapterId);
    }

}
