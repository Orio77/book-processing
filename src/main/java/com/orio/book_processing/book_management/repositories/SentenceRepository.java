package com.orio.book_processing.book_management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.book_management.models.Sentence;

/**
 * Repository for sentence entities parsed from PDFs.
 */
@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    List<Sentence> getByPageNumAndPdfId(int pageNum, Long pdfId);

    List<Sentence> getByPageNumBetweenAndPdfIdAndUserId(int startPage, int endPage, Long pdfId, Long userId);

    List<Sentence> getByPdfIdAndUserId(Long pdfId, Long userId);

    List<Sentence> getByChapterIdAndUserId(Long chapterId, Long userId);
}
