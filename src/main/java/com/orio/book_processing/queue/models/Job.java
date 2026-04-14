package com.orio.book_processing.queue.models;

import com.orio.book_processing.auth.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String errorText;

    private Long resultId;

    @ManyToOne
    private User user;

    public enum JobType {
        PDF_UPLOAD, CHAPTER_SUMMARY, CHAT, IDEA_EXTRACTION, IDEA_EXPLANATION, IDEAS_EXPLANATION
    }

    public enum JobStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
}
