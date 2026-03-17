package com.orio.book_processing.chat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.orio.book_processing.chat.models.ChatResponse;
import com.orio.book_processing.chat.models.ChatResponseContext;

/**
 * Repository for associations between chat responses and context sentences.
 */
@Repository
public interface ChatResponseContextRepository
        extends JpaRepository<ChatResponseContext, ChatResponseContext.ChatResponseContextId> {

    List<ChatResponseContext> findByChatResponseIn(List<ChatResponse> chatResponses);

    List<ChatResponseContext> findByChatResponse(ChatResponse chatResponses);

    Long deleteByChatResponse_Id(Long chatResponseId);

}
