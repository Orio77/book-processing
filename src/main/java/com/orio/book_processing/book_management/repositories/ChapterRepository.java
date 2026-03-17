package com.orio.book_processing.book_management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.book_management.models.Chapter;

/**
 * Repository for chapter entities extracted from PDFs.
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> getByPdfId(Long pdfId);
}
