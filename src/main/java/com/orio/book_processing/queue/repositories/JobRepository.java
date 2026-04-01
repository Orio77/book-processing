package com.orio.book_processing.queue.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {

    Job findFirstByStatus(JobStatus pending);

}
