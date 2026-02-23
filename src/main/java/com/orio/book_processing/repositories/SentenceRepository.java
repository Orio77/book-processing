package com.orio.book_processing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.models.Sentence;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

}
