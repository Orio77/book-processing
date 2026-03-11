package com.orio.book_processing.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatResponseContextRepository
        extends JpaRepository<ChatResponseContext, ChatResponseContext.ChatResponseContextId> {

    List<ChatResponseContext> findByChatResponseIn(List<ChatResponse> chatResponses);

}
