package com.orio.processing.chapter.summary.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class ChapterSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Chapter the summary belongs to (owned by books-service). */
    @Column(nullable = false)
    private Long chapterId;

    @Column(columnDefinition = "TEXT")
    private String summaryText;

    @Column(nullable = false)
    private Long userId;

}
