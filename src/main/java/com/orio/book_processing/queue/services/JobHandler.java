package com.orio.book_processing.queue.services;

import com.orio.book_processing.queue.models.Job.JobType;

public interface JobHandler {

    boolean supports(JobType jobType);

    Long handle(String payload) throws Exception;
}
