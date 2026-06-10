package com.orio.book_processing.queue.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.queue.dtos.JobModelAssembler;
import com.orio.book_processing.queue.dtos.JobResponse;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobQueueController {

    private final JobRepository jobRepo;
    private final JobModelAssembler jobModelAssembler;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return jobRepo.findByIdAndUserId(id, userId)
                .map(jobModelAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/job")
    public ResponseEntity<List<JobResponse>> getJobs(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ResponseEntity.ok(jobRepo.findAllByUserId(userId).stream().map(jobModelAssembler::toModel).toList());
    }

    @PostMapping("/job/{id}/cancel")
    public ResponseEntity<JobResponse> cancelJob(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return jobRepo.findByIdAndUserId(id, userId)
                .map(job -> {
                    if (job.getStatus() != JobStatus.PENDING && job.getStatus() != JobStatus.IN_PROGRESS) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(jobModelAssembler.toModel(job));
                    }
                    job.setStatus(JobStatus.CANCELLED);
                    Job saved = jobRepo.saveAndFlush(job);
                    return ResponseEntity.ok(jobModelAssembler.toModel(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/job/{id}/retry")
    public ResponseEntity<JobResponse> retryJob(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return jobRepo.findByIdAndUserId(id, userId)
                .map(job -> {
                    if (job.getStatus() != JobStatus.FAILED) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(jobModelAssembler.toModel(job));
                    }
                    job.setStatus(JobStatus.PENDING);
                    job.setErrorText(null);
                    job.setResultId(null);
                    Job saved = jobRepo.saveAndFlush(job);
                    return ResponseEntity.ok(jobModelAssembler.toModel(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
