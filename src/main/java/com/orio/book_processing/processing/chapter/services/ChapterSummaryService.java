package com.orio.book_processing.processing.chapter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.chapter.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.repositories.ChapterSummaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChapterSummaryService {

    private final ChapterSummaryRepository chapterSummaryRepo;

    public void save(ChapterSummary chapterSummary) {
        log.info("Saving a chapter summary...");
        chapterSummaryRepo.save(chapterSummary);
        log.info("Chapter summary saved successfully");
    }

    public ChapterSummary saveAndFlush(ChapterSummary chapterSummary) {
        log.info("Saving a chapter summary...");
        ChapterSummary savedChapterSummary = chapterSummaryRepo.saveAndFlush(chapterSummary);
        log.info("Chapter summary saved successfully with id {}", savedChapterSummary.getId());
        return savedChapterSummary;
    }

    public Optional<List<ChapterSummary>> findByChapterId(Long chapterId) {
        log.info("Retrieving sumaries for chapter {}", chapterId);
        Optional<List<ChapterSummary>> chapterSummaries = chapterSummaryRepo.findByChapterId(chapterId);
        if (chapterSummaries.isPresent()) {
            log.info("Retrieved {} summaries for chapter {}", chapterSummaries.get().size(), chapterId);
        } else {
            log.warn("No summaries found for chapter {}", chapterId);
        }
        return chapterSummaries;
    }

    public Optional<ChapterSummary> getReferenceById(Long chapterId) {
        log.info("Retrieving summary for chapter {}", chapterId);
        Optional<ChapterSummary> chapterSummary = Optional.of(chapterSummaryRepo.getReferenceById(chapterId));
        if (chapterSummary.isPresent()) {
            log.info("Summary found for chapter {}", chapterId);
        } else {
            log.warn("Summary for chapter {} not found, returning.", chapterId);
        }
        return chapterSummary;
    }

    public boolean deleteById(Long chapterId) {
        log.info("Deleting chapter {}...", chapterId);
        chapterSummaryRepo.deleteById(chapterId);
        boolean existsById = chapterSummaryRepo.existsById(chapterId);
        boolean isDeleted = !existsById;
        if (isDeleted) {
            log.info("Chapter summary with id {} deleted successfully.");
        } else {
            log.warn("Chapter summary with id {} not found, returning.");
        }
        return isDeleted;
    }
}
