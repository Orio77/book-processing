package com.orio.book_processing.processing.chapter;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.core.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.chapter.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.services.ChapterSummaryService;
import com.orio.book_processing.processing.chapter.services.ISummaryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterSummaryWorkflow {

    private final ISummaryService summaryService;
    private final ChapterService chapterService;
    private final ChapterSummaryService chapterSummaryService;

    public Long generateChapterSummary(Long chapterId) throws LLMGenerationException {
        // Get the chapter
        log.info("Parsing chapter from the database...");
        Chapter chapter = chapterService.getChapter(chapterId);
        log.info("Retrieved chapter {} from the database", chapterId);

        // Create a summary
        log.info("Generating summary for chapter {}...", chapterId);
        String summary = summaryService.generateChapterSummary(chapter.getText());
        log.info("Summary for chapter {} generated.", chapterId);

        // Convert summary text to an object
        ChapterSummary chapterSummary = new ChapterSummary();
        chapterSummary.setChapter(chapter);
        chapterSummary.setSummaryText(summary);

        // Save the summary object
        log.info("Saving chapter summary...");
        ChapterSummary savedChapterSummary = chapterSummaryService.saveAndFlush(chapterSummary);
        log.info("Chapter summary {} saved", chapterSummary.getId());

        // Return the ID
        return savedChapterSummary.getId();
    }
}
