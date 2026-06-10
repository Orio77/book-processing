package com.orio.processing.queue;

public interface JobHandler {

    boolean supports(JobType jobType);

    Long handle(String payload) throws Exception;
}
