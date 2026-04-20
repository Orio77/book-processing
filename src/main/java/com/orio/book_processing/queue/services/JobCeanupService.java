package com.orio.book_processing.queue.services;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobCeanupService {

    private final JobRepository jobRepo;

    @Scheduled(fixedRate = 900000)
    public void cleanupOldJobs() {
        // Calculate the cutoff time: exactly 60 minutes ago
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(60);

        log.info("Starting cleanup of old queue jobs created before {}", cutoff);
        jobRepo.deleteJobsOlderThan(cutoff);
        log.info("Queue jobs cleanup complete.");
    }
}
