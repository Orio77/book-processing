package com.orio.book_processing.book_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.book_management.models.PDF;

/**
 * Repository for PDF entities.
 */
@Repository
public interface PDFRepository extends JpaRepository<PDF, Long> {

}
