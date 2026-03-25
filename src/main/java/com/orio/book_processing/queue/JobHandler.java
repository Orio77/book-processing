package com.orio.book_processing.queue;

import com.orio.book_processing.queue.Job.JobType;

public interface JobHandler {

    boolean supports(JobType jobType);

    void handle(String payload);
}
