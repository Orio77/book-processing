package com.orio.processing.chapter.summary.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.processing.chapter.summary.models.ChapterSummary;

/**
 * Repository for chapter summary entities.
 */
@Repository
public interface ChapterSummaryRepository extends JpaRepository<ChapterSummary, Long> {
    Optional<List<ChapterSummary>> findByChapterIdAndUserId(Long chapterId, Long userId);

    ChapterSummary findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

}
