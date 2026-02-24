package com.orio.book_processing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.models.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    List<Sentence> getByPageNumAndPdfId(int pageNum, Long pdfId);

    List<Sentence> getByPageNumBetweenAndPdfId(int startPage, int endPage, Long pdfId);

    List<Sentence> getByPdfId(Long pdfId);
}
