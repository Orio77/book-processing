package com.orio.book_processing.queue;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private JobType type;

    private JobStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(columnDefinition = "TEXT")
    private String errorText;

    private Long resultId;

    public enum JobType {
        PDF_UPLOAD, CHAPTER_SUMMARY, CHAT, IDEA_EXTRACTION, IDEA_EXPLANATION
    }

    public enum JobStatus {
        PENDING, COMPLETED, CANCELLED, FAILED
    }
}
