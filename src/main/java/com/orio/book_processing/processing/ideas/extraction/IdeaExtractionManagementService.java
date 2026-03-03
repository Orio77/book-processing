package com.orio.book_processing.processing.ideas.extraction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.services.chapter.ChapterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdeaExtractionManagementService {

    private final ChapterService chapterService;

    public List<Idea> extractIdeas(Long chapterId) {

        log.info("Parsing text for chapter {}...", chapterId);
        String chapterText = chapterService.getChapter(chapterId).getText();
        log.info("Chapter text parsed");

        log.info("Extracting ideas from chapter {}...", chapterId);
        // perform idea extraction here
        log.info("Extracted {} ideas from chapter {}", 1, chapterId);

        List<Idea> ideas = new ArrayList<>();
        log.info("Marking {} ideas for chapter {}...", 1, chapterId);
        // perform idea marking here
        log.info("Marked {} ideas for chapter {} successfully", 1, chapterId);

        return ideas;

    }
}
