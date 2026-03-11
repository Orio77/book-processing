package com.orio.book_processing.chat.models;

import java.io.Serializable;

import com.orio.book_processing.book_management.models.Sentence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@IdClass(ChatResponseContext.ChatResponseContextId.class)
public class ChatResponseContext {

    @Id
    @ManyToOne
    @JoinColumn(name = "chat_response_id", nullable = false)
    private ChatResponse chatResponse;

    @Id
    @ManyToOne
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @Data
    @NoArgsConstructor
    public static class ChatResponseContextId implements Serializable {
        private Long chatResponse;
        private Long sentence;
    }

}
