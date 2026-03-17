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

import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaArgumentDTO;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.book_processing.processing.ideas.extraction.services.IdeaExtractionManagementService;
import com.orio.book_processing.processing.ideas.extraction.services.wrappers.IdeaArgumentService;
import com.orio.book_processing.processing.ideas.extraction.services.wrappers.IdeaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST endpoints for extracting ideas from chapter sentences and managing
 * extracted ideas and arguments.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/pdf/process/idea")
public class IdeaController {

    private final IdeaExtractionManagementService ideaExtractionManagementService;
    private final IdeaService ideaService;
    private final IdeaArgumentService ideaArgumentService;

    @PostMapping("/extract")
    public ResponseEntity<?> extractIdeasByChapterId(@RequestParam Long chapterId) {

        IdeaExtractionAiResponse result = ideaExtractionManagementService.extractIdeas(chapterId);
        log.info("extraction completed");

        return ResponseEntity.ok(result.ideaContainers().size());
    }

    @GetMapping("/get/all/{chapterId}")
    public ResponseEntity<List<IdeaWithSentences>> getAllIdeasByChapterId(@PathVariable Long chapterId) {
        return ideaService.getIdeasByChapter(chapterId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/{ideaId}")
    public ResponseEntity<IdeaWithSentences> getIdeaById(@PathVariable Long ideaId) {
        return ideaService.getIdea(ideaId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/argument/get/{ideaId}")
    public ResponseEntity<List<IdeaArgumentDTO>> getArgumentsForIdea(@PathVariable Long ideaId) {
        return ideaArgumentService.getIdeaArgumentsForIdea(ideaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{ideaId}")
    public ResponseEntity<Boolean> deleteIdeaById(@PathVariable Long ideaId) {
        return ideaService.deleteIdea(ideaId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}
