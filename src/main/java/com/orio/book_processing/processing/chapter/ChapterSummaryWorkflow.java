package com.orio.book_processing.processing.chapter;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.processing.chapter.exceptions.LLMGenerationException;
import com.orio.book_processing.processing.chapter.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.services.ChapterSummaryService;
import com.orio.book_processing.processing.chapter.services.ISummaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterSummaryWorkflow {

    private final ISummaryService summaryService;
    private final ChapterService chapterService;
    private final ChapterSummaryService chapterSummaryService;

    public Long generateChapterSummary(Long chapterId) throws LLMGenerationException {
        // Get the chapter
        Chapter chapter = chapterService.getChapter(chapterId);

        // Create a summary
        String summary = summaryService.generateChapterSummary(chapter.getText());

        // Convert summary text to an object
        ChapterSummary chapterSummary = new ChapterSummary();
        chapterSummary.setChapter(chapter);
        chapterSummary.setSummaryText(summary);

        // Save the summary object
        ChapterSummary savedChapterSummary = chapterSummaryService.saveAndFlush(chapterSummary);

        // Return the ID
        return savedChapterSummary.getId();
    }
}
