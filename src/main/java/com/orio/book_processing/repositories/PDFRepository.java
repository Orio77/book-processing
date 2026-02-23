package com.orio.book_processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.models.PDF;

@Repository
public interface PDFRepository extends JpaRepository<PDF, Long> {

}
