package com.orio.book_processing.processing;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.services.chapter.ChapterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterSummaryWorkflow {

    private final ISummaryService summaryService;
    private final ChapterService chapterService;
    private final ChapterSummaryService chapterSummaryService;

    public Long generateChapterSummary(Long chapterId) {
        Chapter chapter = chapterService.getChapter(chapterId);
        String summary = summaryService.generateChapterSummary(chapter.getText());
        ChapterSummary chapterSummary = new ChapterSummary();
        chapterSummary.setChapter(chapter);
        chapterSummary.setSummaryText(summary);
        ChapterSummary savedChapterSummary = chapterSummaryService.saveAndFlush(chapterSummary);
        return savedChapterSummary.getId();
    }
}
