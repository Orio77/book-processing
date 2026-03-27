package com.orio.book_processing.processing.chapter.summary;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.chapter.summary.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.summary.services.ISummaryService;
import com.orio.book_processing.processing.chapter.summary.services.wrappers.ChapterSummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates chapter summarization by loading chapter content, generating a
 * summary through the LLM service, and saving the resulting summary entity.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterSummaryWorkflow {

    private final ISummaryService summaryService;
    private final ChapterService chapterService;
    private final ChapterSummaryService chapterSummaryService;

    public Long generateChapterSummary(Long chapterId) throws LLMGenerationException {
        log.info("Parsing chapter from the database...");
        Chapter chapter = chapterService.getChapterEagerly(chapterId);
        log.info("Retrieved chapter {} from the database", chapterId);

        log.info("Generating summary for chapter {}...", chapterId);
        String summary = summaryService.generateChapterSummary(chapter.getText());
        log.info("Summary for chapter {} generated.", chapterId);

        // Build a persistable ChapterSummary entity
        ChapterSummary chapterSummary = new ChapterSummary();
        chapterSummary.setChapter(chapter);
        chapterSummary.setSummaryText(summary);

        log.info("Saving chapter summary...");
        ChapterSummary savedChapterSummary = chapterSummaryService.saveAndFlush(chapterSummary);
        log.info("Chapter summary {} saved", chapterSummary.getId());

        return savedChapterSummary.getId();
    }
}
