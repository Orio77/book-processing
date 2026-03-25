package com.orio.book_processing.queue;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.queue.Job.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {

    Job findFirstByStatus(JobStatus pending);

}
