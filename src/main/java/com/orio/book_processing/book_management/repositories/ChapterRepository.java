package com.orio.book_processing.book_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.book_management.models.Chapter;

/**
 * Repository for chapter entities extracted from PDFs.
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> getByPdfId(Long pdfId);

    @Query("SELECT DISTINCT c FROM Chapter c LEFT JOIN FETCH c.sentences WHERE c.id = :chapterId AND c.user.id = :userId")
    Optional<Chapter> findByIdAndUserIdWithSentences(@Param("chapterId") Long chapterId, @Param("userId") Long userId);

    Optional<Chapter> findByIdAndUserId(Long id, Long userId);

    List<Chapter> getByPdfIdAndUserId(Long pdfId, Long userId);
}
