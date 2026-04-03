package com.orio.book_processing.queue.controllers;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.orio.book_processing.queue.events.JobCompletionEvent;
import com.orio.book_processing.queue.models.Job;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobQueueController {

    private final JobRepository jobRepo;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void notifyJobCompletion(JobCompletionEvent jobCompletionEvent) {
        messagingTemplate.convertAndSend("/topic/jobs/completed", jobCompletionEvent.jobId());
    }

    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        return jobRepo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/job")
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(jobRepo.findAll());
    }

}
