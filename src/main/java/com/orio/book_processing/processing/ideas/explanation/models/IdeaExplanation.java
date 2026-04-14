package com.orio.book_processing.processing.ideas.explanation.models;

import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class IdeaExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Idea idea;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    private User user;
}
