package com.orio.book_processing.processing;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterSummaryRepository extends JpaRepository<ChapterSummary, Long> {
    Optional<List<ChapterSummary>> findByChapterId(Long chapterId);

}
