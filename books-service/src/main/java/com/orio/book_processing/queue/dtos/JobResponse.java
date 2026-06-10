package com.orio.book_processing.queue.dtos;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;

import lombok.Getter;

/**
 * HAL representation of a queued job. Links depend on status: cancel while
 * PENDING/IN_PROGRESS, result when COMPLETED, retry when FAILED.
 */
@Getter
public class JobResponse extends RepresentationModel<JobResponse> {

    private final Long id;
    private final JobType type;
    private final JobStatus status;
    private final Long resultId;
    private final String errorText;
    private final LocalDateTime createdAt;

    public JobResponse(Job job) {
        this.id = job.getId();
        this.type = job.getType();
        this.status = job.getStatus();
        this.resultId = job.getResultId();
        this.errorText = job.getErrorText();
        this.createdAt = job.getCreatedAt();
    }
}
