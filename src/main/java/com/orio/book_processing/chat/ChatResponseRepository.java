package com.orio.book_processing.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatResponseRepository extends JpaRepository<ChatResponse, Long> {

    List<ChatResponse> getByChapterId(Long chapterId);
}
