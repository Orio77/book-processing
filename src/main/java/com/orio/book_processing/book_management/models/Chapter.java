package com.orio.book_processing.book_management.models;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import com.orio.book_processing.auth.User;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_chapter_pdf_id", columnList = "pdf_id")
})
@NoArgsConstructor
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String title;

    @Column(nullable = false)
    private int startPage;

    @Column(nullable = false)
    private int endPage;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pdf_id", nullable = false)
    private PDF pdf;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Sentence> sentences = new ArrayList<>();

    public String getText() {
        return sentences.stream()
                .map(Sentence::getContent)
                .collect(Collectors.joining());
    }
}
