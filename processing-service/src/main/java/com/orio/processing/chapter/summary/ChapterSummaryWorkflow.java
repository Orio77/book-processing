package com.orio.processing.chapter.summary;

import org.springframework.stereotype.Service;

import com.orio.processing.chapter.summary.dtos.ChapterSummaryRequest;
import com.orio.processing.chapter.summary.models.ChapterSummary;
import com.orio.processing.chapter.summary.services.ISummaryService;
import com.orio.processing.chapter.summary.services.wrappers.ChapterSummaryService;
import com.orio.processing.core.exceptions.LLMGenerationException;
import com.orio.processing.grpc.BooksClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates chapter summarization: loads chapter text from books-service
 * over gRPC, generates a summary through the LLM service, and saves the
 * resulting summary entity in the processing schema.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterSummaryWorkflow {

    private final ISummaryService summaryService;
    private final BooksClient booksClient;
    private final ChapterSummaryService chapterSummaryService;

    public Long generateChapterSummary(ChapterSummaryRequest request) throws LLMGenerationException {
        Long chapterId = request.chapterId();
        Long userId = request.userId();

        log.info("Fetching chapter {} text over gRPC...", chapterId);
        String chapterText = booksClient.getChapterText(chapterId, userId).getText();
        log.info("Retrieved chapter {} text from books-service", chapterId);

        log.info("Generating summary for chapter {}...", chapterId);
        String summary = summaryService.generateChapterSummary(chapterText);
        log.info("Summary for chapter {} generated.", chapterId);

        ChapterSummary chapterSummary = new ChapterSummary();
        chapterSummary.setChapterId(chapterId);
        chapterSummary.setSummaryText(summary);
        chapterSummary.setUserId(userId);

        log.info("Saving chapter summary...");
        ChapterSummary savedChapterSummary = chapterSummaryService.saveAndFlush(chapterSummary);
        log.info("Chapter summary {} saved", savedChapterSummary.getId());

        return savedChapterSummary.getId();
    }
}
