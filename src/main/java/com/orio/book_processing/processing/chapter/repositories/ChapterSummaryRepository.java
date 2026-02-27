package com.orio.book_processing.processing.chapter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.processing.chapter.models.ChapterSummary;

public interface ChapterSummaryRepository extends JpaRepository<ChapterSummary, Long> {
    Optional<List<ChapterSummary>> findByChapterId(Long chapterId);

}
