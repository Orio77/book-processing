package com.orio.book_processing.book_management.dtos.response;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.queue.models.Job.JobStatus;
import com.orio.book_processing.queue.models.Job.JobType;
import com.orio.book_processing.queue.repositories.JobRepository;

import lombok.RequiredArgsConstructor;

/**
 * Builds HAL representations of chapters with conditional links: when a
 * processing result already exists the link points at the result
 * (summary/ideas), otherwise at the action that generates it
 * (generate-summary/extract-ideas). Existence is derived from completed jobs
 * in the queue table; links are gateway-relative since results are served by
 * processing-service.
 */
@Component
@RequiredArgsConstructor
public class ChapterModelAssembler {

    private final JobRepository jobRepo;

    public EntityModel<ChapterResponse> toModel(Chapter chapter, Long userId) {
        Long chapterId = chapter.getId();
        EntityModel<ChapterResponse> model = EntityModel.of(ChapterResponse.from(chapter));

        model.add(Link.of("/api/pdf/chapter/get/" + chapterId).withSelfRel());

        String payloadFragment = "\"chapterId\":" + chapterId;

        if (hasCompleted(userId, JobType.CHAPTER_SUMMARY, payloadFragment)) {
            model.add(Link.of("/api/pdf/process/chapter/" + chapterId + "/summary", "summary"));
        } else {
            model.add(Link.of("/api/pdf/process/chapter/summary?chapterId=" + chapterId, "generate-summary"));
        }

        if (hasCompleted(userId, JobType.IDEA_EXTRACTION, payloadFragment)) {
            model.add(Link.of("/api/pdf/process/idea/get/all/" + chapterId, "ideas"));
        } else {
            model.add(Link.of("/api/pdf/process/idea/extract?chapterId=" + chapterId, "extract-ideas"));
        }

        return model;
    }

    private boolean hasCompleted(Long userId, JobType type, String payloadFragment) {
        return jobRepo.existsByUserIdAndTypeAndStatusAndPayloadContaining(userId, type, JobStatus.COMPLETED,
                payloadFragment);
    }
}
