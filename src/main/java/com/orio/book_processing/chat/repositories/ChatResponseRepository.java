package com.orio.book_processing.chat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.chat.models.ChatResponse;

/**
 * Repository for persisted chat responses.
 */
@Repository
public interface ChatResponseRepository extends JpaRepository<ChatResponse, Long> {

    List<ChatResponse> getByChapterId(Long chapterId);
}
