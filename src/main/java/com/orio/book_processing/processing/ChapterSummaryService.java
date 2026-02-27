package com.orio.book_processing.processing;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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

    public ChapterSummary getReferenceById(Long id) throws EntityNotFoundException {
        return chapterSummaryRepo.getReferenceById(id);
    }
}
