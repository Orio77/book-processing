package com.orio.book_processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.models.Sentence;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {

}
