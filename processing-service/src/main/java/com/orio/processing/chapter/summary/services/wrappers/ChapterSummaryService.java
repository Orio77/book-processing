package com.orio.processing.chapter.summary.services.wrappers;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orio.processing.chapter.summary.models.ChapterSummary;
import com.orio.processing.chapter.summary.repositories.ChapterSummaryRepository;

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

    public Optional<List<ChapterSummary>> findByChapterIdAndUserId(Long chapterId, Long userId) {
        log.info("Retrieving sumaries for chapter {}", chapterId);
        Optional<List<ChapterSummary>> chapterSummaries = chapterSummaryRepo.findByChapterIdAndUserId(chapterId,
                userId);
        if (chapterSummaries.isPresent()) {
            log.info("Retrieved {} summaries for chapter {}", chapterSummaries.get().size(), chapterId);
        } else {
            log.warn("No summaries found for chapter {}", chapterId);
        }
        return chapterSummaries;
    }

    public Optional<ChapterSummary> findByIdAndUserId(Long chapterSummaryId, Long userId) {
        log.info("Retrieving summary for chapter {}", chapterSummaryId);
        Optional<ChapterSummary> chapterSummary = Optional
                .of(chapterSummaryRepo.findByIdAndUserId(chapterSummaryId, userId));
        if (chapterSummary.isPresent()) {
            log.info("Summary found for chapter {}", chapterSummaryId);
        } else {
            log.warn("Summary for chapter {} not found, returning.", chapterSummaryId);
        }
        return chapterSummary;
    }

    public boolean deleteByIdAndUserId(Long chapterId, Long userId) {
        log.info("Deleting chapter {}...", chapterId);
        chapterSummaryRepo.deleteByIdAndUserId(chapterId, userId);
        boolean existsById = chapterSummaryRepo.existsById(chapterId);
        boolean isDeleted = !existsById;
        if (isDeleted) {
            log.info("Chapter summary with id {} deleted successfully.", chapterId);
        } else {
            log.warn("Chapter summary with id {} not found, returning.", chapterId);
        }
        return isDeleted;
    }
}
