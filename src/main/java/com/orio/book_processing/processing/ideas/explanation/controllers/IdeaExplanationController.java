package com.orio.book_processing.processing.ideas.explanation.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.processing.ideas.explanation.dtos.IdeaExplanationDTO;
import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.book_processing.processing.ideas.explanation.services.IdeaExplanationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf/process/idea")
public class IdeaExplanationController {

    private final IdeaExplanationService ideaExplanationService;

    @PostMapping("/{ideaId}/explanation")
    public ResponseEntity<?> createExplanation(@PathVariable Long ideaId,
            @RequestBody String ideaContent) {
        log.info("Explanation creation request received for idea {}.", ideaId);
        if (ideaContent == null || ideaContent.isBlank()) {
            return ResponseEntity.badRequest().body("Explanation content must not be blank.");
        }

        Optional<IdeaExplanation> explanation = ideaExplanationService.createExplanation(ideaId, ideaContent);
        return explanation
                .map(exp -> ResponseEntity.status(HttpStatus.CREATED).body(IdeaExplanationDTO.from(exp.getId(),
                        exp.getIdea(), exp.getText())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{ideaId}/explanations")
    public ResponseEntity<List<IdeaExplanationDTO>> getExplanationsForIdea(@PathVariable Long ideaId) {
        log.info("Explanations fetch for idea {} request received.", ideaId);
        Optional<List<IdeaExplanation>> explanations = ideaExplanationService.getExplanationsForIdea(ideaId);
        return explanations.map(exps -> ResponseEntity.ok(exps.stream()
                .map(exp -> IdeaExplanationDTO.from(exp.getId(), exp.getIdea(), exp.getText()))
                .toList()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/explanations/{explanationId}")
    public ResponseEntity<?> getIdeaExplanation(@PathVariable Long explanationId) {
        log.info("Explanation fetch with id {} received.", explanationId);
        Optional<IdeaExplanation> explanation = ideaExplanationService.getIdeaExplanation(explanationId);
        return explanation.map(exp -> ResponseEntity.ok(IdeaExplanationDTO.from(exp.getId(),
                exp.getIdea(), exp.getText()))).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/explanations/{explanationId}")
    public ResponseEntity<IdeaExplanationDTO> updateIdeaExplanation(@PathVariable Long explanationId,
            @RequestBody String newExplanationContent) {
        log.info("Explanation update request received.");
        Optional<IdeaExplanation> explanation = ideaExplanationService.update(explanationId, newExplanationContent);
        return explanation.map(
                exp -> ResponseEntity.ok(IdeaExplanationDTO.from(exp.getId(),
                        exp.getIdea(), exp.getText())))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/explanations/{explanationId}")
    public ResponseEntity<Void> deleteIdeaExplanation(@PathVariable Long explanationId) {
        log.info("Explanation delete request received for id {}.", explanationId);
        boolean deleted = ideaExplanationService.delete(explanationId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
