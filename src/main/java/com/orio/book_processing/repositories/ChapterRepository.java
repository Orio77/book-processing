package com.orio.book_processing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.models.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> getByPdfId(Long pdfId);
}
