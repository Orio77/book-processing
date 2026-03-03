package com.orio.book_processing.processing.ideas.extraction.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaResponse;
import com.orio.book_processing.processing.ideas.extraction.services.IdeaExtractionManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/pdf/process/idea")
public class IdeaController {

    private final IdeaExtractionManagementService ideaExtractionManagementService;

    @PostMapping("/extract")
    public ResponseEntity<?> extractIdeasByChapterId(@RequestParam Long chapterId) {

        List<Idea> ideas = ideaExtractionManagementService.extractIdeas(chapterId);
        log.info("extraction completed");
        return ResponseEntity.ok(ideas);

    }

    @GetMapping("/get/all/{chapterId}")
    public ResponseEntity<List<IdeaResponse>> getAllIdeasByChapterId(@PathVariable Long chapterId) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/get/{ideaId}")
    public ResponseEntity<IdeaResponse> getIdeaById(@PathVariable Long ideaId) {
        return ResponseEntity.ok(IdeaResponse.from(null, null));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteIdeaById(@RequestParam Long ideaId) {
        return ResponseEntity.ok(true);
    }

}
