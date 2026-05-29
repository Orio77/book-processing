package com.orio.book_processing.queue.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;

import jakarta.transaction.Transactional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Job findFirstByStatus(JobStatus pending);

    List<Job> findAllByUserId(Long userId);

    Optional<Job> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.createdAt < :cutoffTime")
    void deleteJobsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

}
