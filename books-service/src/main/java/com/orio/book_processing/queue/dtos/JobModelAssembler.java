package com.orio.book_processing.queue.dtos;

import java.util.Optional;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orio.book_processing.queue.models.Job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Builds HAL links for jobs. Result links are gateway-relative so they remain
 * valid regardless of which service serves the result.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobModelAssembler {

    private final ObjectMapper objectMapper;

    public JobResponse toModel(Job job) {
        JobResponse model = new JobResponse(job);

        model.add(Link.of("/api/job/" + job.getId()).withSelfRel());

        switch (job.getStatus()) {
            case PENDING, IN_PROGRESS -> model.add(Link.of("/api/job/" + job.getId() + "/cancel", "cancel"));
            case COMPLETED -> resultLink(job).ifPresent(model::add);
            case FAILED -> model.add(Link.of("/api/job/" + job.getId() + "/retry", "retry"));
            case CANCELLED -> {
            }
        }

        return model;
    }

    private Optional<Link> resultLink(Job job) {
        Long resultId = job.getResultId();
        if (resultId == null) {
            return Optional.empty();
        }

        // For IDEA_EXTRACTION / IDEAS_EXPLANATION the resultId is the chapterId.
        return switch (job.getType()) {
            case PDF_UPLOAD -> Optional.of(Link.of("/api/pdf/get/" + resultId, "result"));
            case CHAPTER_SUMMARY -> Optional.of(Link.of("/api/pdf/process/chapter/summary/" + resultId, "result"));
            case IDEA_EXTRACTION, IDEAS_EXPLANATION ->
                Optional.of(Link.of("/api/pdf/process/idea/get/all/" + resultId, "result"));
            case IDEA_EXPLANATION ->
                Optional.of(Link.of("/api/pdf/process/idea/explanations/" + resultId, "result"));
            case CHAT -> chatChapterId(job)
                    .map(chapterId -> Link.of("/api/pdf/chat/response/get/all/" + chapterId, "result"));
        };
    }

    private Optional<Long> chatChapterId(Job job) {
        try {
            JsonNode chapterId = objectMapper.readTree(job.getPayload()).get("chapterId");
            return chapterId == null ? Optional.empty() : Optional.of(chapterId.asLong());
        } catch (Exception e) {
            log.warn("Could not parse chapterId from CHAT job {} payload", job.getId());
            return Optional.empty();
        }
    }
}
