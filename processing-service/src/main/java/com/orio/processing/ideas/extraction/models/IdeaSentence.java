package com.orio.processing.ideas.extraction.models;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Links an idea to a source sentence by id (sentences are owned by
 * books-service; contents are fetched over gRPC when needed).
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(IdeaSentence.IdeaSentenceId.class)
public class IdeaSentence {

    @Id
    @ManyToOne
    @JoinColumn(name = "idea_id", nullable = false)
    private Idea idea;

    @Id
    private Long sentenceId;

    @Data
    @NoArgsConstructor
    public static class IdeaSentenceId implements Serializable {

        private Long idea;
        private Long sentenceId;
    }
}
