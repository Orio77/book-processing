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

import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaWithSentences;
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

        IdeaExtractionAiResponse result = ideaExtractionManagementService.extractIdeas(chapterId);
        log.info("extraction completed");

        return ResponseEntity.ok(
                "Ideas created: %d".formatted(result.ideaContainers().size()));
    }

    @GetMapping("/get/all/{chapterId}")
    public ResponseEntity<List<IdeaWithSentences>> getAllIdeasBychapterId(@PathVariable Long chapterId) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get/{ideaId}")
    public ResponseEntity<IdeaWithSentences> getIdeaById(@PathVariable Long ideaId) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/argument/get/{ideaId}")
    public ResponseEntity<List<IdeaArgument>> getArgumentsForIdea(@PathVariable Long ideaId) {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/delete/{ideaId}")
    public ResponseEntity<Boolean> deleteIdeaById(@PathVariable Long ideaId) {
        return ResponseEntity.ok(true);
    }

}
