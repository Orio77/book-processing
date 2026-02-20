package com.orio.book_processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.models.PDF;

public interface PDFRepository extends JpaRepository<PDF, Long> {

}
