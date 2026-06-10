package com.orio.book_processing.queue.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Claims jobs from the PostgreSQL-backed queue. The claim (SELECT ... FOR
 * UPDATE SKIP LOCKED + status flip to IN_PROGRESS) commits before the job is
 * executed, so the row lock is held only briefly and a crashed worker leaves
 * the job visibly IN_PROGRESS instead of silently locked.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobClaimService {

    private final JobRepository jobRepo;

    @Transactional
    public Optional<Job> claimNext(Collection<JobType> types) {
        Optional<Job> maybeJob = jobRepo.findNextPendingByTypes(types.stream().map(Enum::name).toList());

        maybeJob.ifPresent(job -> {
            job.setStatus(JobStatus.IN_PROGRESS);
            log.info("Claimed job {} of type {}", job.getId(), job.getType());
        });

        return maybeJob;
    }
}
