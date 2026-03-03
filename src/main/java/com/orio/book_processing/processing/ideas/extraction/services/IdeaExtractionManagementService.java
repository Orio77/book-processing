package com.orio.book_processing.processing.ideas.extraction.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final ChapterService chapterService;
    private final IdeaRepository ideaRepo;

    private final IdeaExtractionService extractionService;
    private final IdeaMarkingService ideaMarkingService;

    @Transactional
    public List<Idea> extractIdeas(Long chapterId) {

        String chapterText = getChapterText(chapterId);

        List<Idea> ideas = extractionService.getIdeas(chapterText);

        ideaRepo.saveAll(ideas);

        ideaMarkingService.markIdeas(chapterId, ideas);

        return ideas;

    }

    private String getChapterText(Long chapterId) {
        log.info("Parsing text for chapter {}...", chapterId);
        String chapterText = chapterService.getChapter(chapterId).getText();
        if (chapterText.length() > 5000) {
            log.info("Chapter text too long - {} chars, trimming...", chapterText.length());
            chapterText = chapterText.substring(0, 5000);
        }
        log.info("Chapter text parsed");
        return chapterText;
    }

}
