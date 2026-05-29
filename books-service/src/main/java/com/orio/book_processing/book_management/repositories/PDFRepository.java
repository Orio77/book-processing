package com.orio.book_processing.book_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.book_management.models.PDF;

/**
 * Repository for PDF entities.
 */
@Repository
public interface PDFRepository extends JpaRepository<PDF, Long> {

    Optional<PDF> findByIdAndUserId(Long id, Long userId);

    List<PDF> findAllByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);
}
