package com.orio.book_processing.processing.ideas.extraction;

import java.io.Serializable;

import com.orio.book_processing.book_management.models.Sentence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @ManyToOne
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @Data
    @NoArgsConstructor
    public static class IdeaSentenceId implements Serializable {

        private Long idea;
        private Long sentence;
    }
}
