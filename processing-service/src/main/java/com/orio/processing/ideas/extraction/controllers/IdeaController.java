package com.orio.processing.ideas.extraction.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.processing.ideas.extraction.dtos.IdeaArgumentDTO;
import com.orio.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.processing.ideas.extraction.services.wrappers.IdeaArgumentService;
import com.orio.processing.ideas.extraction.services.wrappers.IdeaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Read/delete endpoints for extracted ideas and arguments. Extraction itself
 * is enqueued in books-service (POST /api/pdf/process/** routes there).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/pdf/process/idea")
public class IdeaController {

    private final IdeaService ideaService;
    private final IdeaArgumentService ideaArgumentService;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @GetMapping("/get/all/{chapterId}")
    public ResponseEntity<List<IdeaWithSentences>> getAllIdeasByChapterId(@PathVariable Long chapterId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ideaService.getIdeasByChapter(chapterId, userId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get/{ideaId}")
    public ResponseEntity<IdeaWithSentences> getIdeaById(@PathVariable Long ideaId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        try {
            return ResponseEntity.ok(ideaService.getIdea(ideaId, userId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/argument/get/{ideaId}")
    public ResponseEntity<List<IdeaArgumentDTO>> getArgumentsForIdea(@PathVariable Long ideaId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ideaArgumentService.getIdeaArgumentsForIdea(ideaId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{ideaId}")
    public ResponseEntity<Boolean> deleteIdeaById(@PathVariable Long ideaId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        return ideaService.deleteIdea(ideaId, userId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
