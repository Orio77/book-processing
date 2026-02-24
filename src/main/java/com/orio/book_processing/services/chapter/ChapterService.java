package com.orio.book_processing.services.chapter;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.repositories.ChapterRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepo;

    public List<Chapter> createChapters(PDF pdf, List<PageRange> chapterPageRanges) {

        List<Chapter> chapters = chapterPageRanges.stream().map((PageRange pageRange) -> {
            Chapter chapter = new Chapter();
            chapter.setPdf(pdf);
            chapter.setStartPage(pageRange.startPage());
            chapter.setEndPage(pageRange.endPage());
            return chapter;
        }).toList();

        log.info("Created {} chapters out of {} page ranges", chapters.size(), chapterPageRanges.size());
        return chapters;
    }

    public void saveChapters(List<Chapter> chapters) {
        log.info("Saving {} chapters...", chapters.size());
        chapterRepo.saveAll(chapters);
    }

    public int getChapterIndex(int pageIndex, List<PageRange> chapterPageRanges) {
        log.info("Looking for chapter for page {}", pageIndex);
        // Find the range wihtin which the page is placed
        for (int i = 0; i < chapterPageRanges.size(); i++) {
            log.debug("Looking for page {} in range {} - {}", pageIndex, chapterPageRanges.get(i).startPage(),
                    chapterPageRanges.get(i).endPage());
            if (pageIndex >= chapterPageRanges.get(i).startPage() && pageIndex <= chapterPageRanges.get(i).endPage()) {
                log.info("Found chapter for page {} in range {} - {}", pageIndex, chapterPageRanges.get(i).startPage(),
                        chapterPageRanges.get(i).endPage());
                return i;
            }
        }

        log.error("Couldn't find page {} in chapter ranges: {}", pageIndex, chapterPageRanges);
        // When page isn't placed within any of chapter page ranges
        throw new IllegalArgumentException("Invalid page range");
    }

    public Chapter getChapter(Long id) throws EntityNotFoundException {
        return chapterRepo.getReferenceById(id);
    }

    public List<Chapter> getAllChapters(Long pdfId) {
        return chapterRepo.getByPdfId(pdfId);
    }
}
