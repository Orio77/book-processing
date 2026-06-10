package com.orio.book_processing.queue.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.orio.book_processing.queue.models.Job;

import jakarta.transaction.Transactional;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByUserId(Long userId);

    Optional<Job> findByIdAndUserId(Long id, Long userId);

    /**
     * Atomically picks the oldest PENDING job of one of the given types. The row
     * stays locked until the surrounding transaction commits; concurrent claimers
     * skip locked rows, so a job is handed out exactly once. Same query the gRPC
     * ClaimNextJob endpoint will use.
     */
    @Query(value = """
            SELECT * FROM job
            WHERE status = 'PENDING' AND type IN (:types)
            ORDER BY created_at
            LIMIT 1
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    Optional<Job> findNextPendingByTypes(@Param("types") Collection<String> types);

    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.createdAt < :cutoffTime")
    void deleteJobsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

}
