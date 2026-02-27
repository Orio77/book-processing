package com.orio.book_processing.processing.chapter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.orio.book_processing.processing.chapter.models.ChapterSummary;
import com.orio.book_processing.processing.chapter.repositories.ChapterSummaryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterSummaryService {

    private final ChapterSummaryRepository chapterSummaryRepo;

    public void save(ChapterSummary chapterSummary) {
        chapterSummaryRepo.save(chapterSummary);
    }

    public ChapterSummary saveAndFlush(ChapterSummary chapterSummary) {
        return chapterSummaryRepo.saveAndFlush(chapterSummary);
    }

    public Optional<List<ChapterSummary>> findByChapterId(Long chapterId) {
        return chapterSummaryRepo.findByChapterId(chapterId);
    }

    public Optional<ChapterSummary> getReferenceById(Long id) {
        return Optional.of(chapterSummaryRepo.getReferenceById(id));
    }

    public boolean deleteById(Long id) {
        chapterSummaryRepo.deleteById(id);
        return !chapterSummaryRepo.existsById(id);
    }
}
