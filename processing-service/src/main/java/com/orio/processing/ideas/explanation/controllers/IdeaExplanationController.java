package com.orio.processing.ideas.explanation.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.processing.ideas.explanation.dtos.IdeaExplanationResponse;
import com.orio.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.processing.ideas.explanation.services.IdeaExplanationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Read/update/delete endpoints for idea explanations. Generation is enqueued
 * in books-service (POST /api/pdf/process/** routes there).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process/idea")
public class IdeaExplanationController {

    private final IdeaExplanationService ideaExplanationService;

    private Long currentUserId(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    @GetMapping("/{ideaId}/explanations")
    public ResponseEntity<List<IdeaExplanationResponse>> getExplanationsForIdea(@PathVariable Long ideaId,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Explanations fetch for idea {} request received.", ideaId);
        Long userId = currentUserId(jwt);
        Optional<List<IdeaExplanation>> explanations = ideaExplanationService.getExplanationsForIdea(ideaId, userId);
        return explanations.map(exps -> ResponseEntity.ok(exps.stream()
                .map(exp -> IdeaExplanationResponse.from(exp.getId(), exp.getIdea(), exp.getText()))
                .toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/explanations/{explanationId}")
    public ResponseEntity<?> getIdeaExplanation(@PathVariable Long explanationId, @AuthenticationPrincipal Jwt jwt) {
        log.info("Explanation fetch with id {} received.", explanationId);
        Long userId = currentUserId(jwt);
        Optional<IdeaExplanation> explanation = ideaExplanationService.getIdeaExplanation(explanationId, userId);
        return explanation.map(exp -> ResponseEntity.ok(IdeaExplanationResponse.from(exp.getId(),
                exp.getIdea(), exp.getText()))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/explanations/{explanationId}")
    public ResponseEntity<IdeaExplanationResponse> updateIdeaExplanation(@PathVariable Long explanationId,
            @RequestBody String newExplanationContent, @AuthenticationPrincipal Jwt jwt) {
        log.info("Explanation update request received.");
        Long userId = currentUserId(jwt);
        Optional<IdeaExplanation> explanation = ideaExplanationService.update(explanationId, newExplanationContent,
                userId);
        return explanation.map(
                exp -> ResponseEntity.ok(IdeaExplanationResponse.from(exp.getId(),
                        exp.getIdea(), exp.getText())))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/explanations/{explanationId}")
    public ResponseEntity<Void> deleteIdeaExplanation(@PathVariable Long explanationId,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("Explanation delete request received for id {}.", explanationId);
        Long userId = currentUserId(jwt);
        boolean deleted = ideaExplanationService.delete(explanationId, userId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
