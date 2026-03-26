package com.orio.book_processing.queue;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@Controller
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
        ResponseEntity<Job> result = jobRepo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        jobRepo.deleteById(id);
        return result;
    }

    @GetMapping("/job")
    public ResponseEntity<List<Job>> getJobs() {
        return ResponseEntity.ok(jobRepo.findAll());
    }

}
