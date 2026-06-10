package com.orio.book_processing.queue.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobQueueController {

    private final JobRepository jobRepo;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return jobRepo.findByIdAndUserId(id, userId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/job")
    public ResponseEntity<List<Job>> getJobs(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ResponseEntity.ok(jobRepo.findAllByUserId(userId));
    }

}
