package com.orio.book_processing.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_sentence_pdf_id", columnList = "pdf_id"),
        @Index(name = "idx_sentence_chapter_id", columnList = "chapter_id"),
        @Index(name = "idx_sentence_page_num", columnList = "pdf_id, pageNum")
})
public class Sentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int sentenceNum;

    @Column(nullable = false)
    private int pageNum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pdf_id", nullable = false)
    private PDF pdf;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
}
