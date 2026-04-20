package com.orio.book_processing.book_management.services.chapter;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;

import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.repositories.ChapterRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepo;
    private final UserRepository userRepo;

    public List<Chapter> createChapters(PDF pdf, List<PageRange> chapterPageRanges) {

        List<Chapter> chapters = chapterPageRanges.stream()
                .filter(getPageRangePredicates(pdf.getTotalPages()))
                .map((PageRange pageRange) -> {
                    Chapter chapter = new Chapter();
                    chapter.setPdf(pdf);
                    chapter.setStartPage(pageRange.startPage());
                    chapter.setEndPage(pageRange.endPage());
                    return chapter;
                }).toList();

        log.info("Created {} chapters out of {} page ranges", chapters.size(), chapterPageRanges.size());
        return chapters;
    }

    public void saveChapters(List<Chapter> chapters, Long userId) {
        log.info("Saving {} chapters...", chapters.size());
        User user = userRepo.getReferenceById(userId);
        chapters.forEach(ch -> ch.setUser(user));
        chapterRepo.saveAll(chapters);
    }

    public Optional<Chapter> getChapterForPage(int pageIndex, List<Chapter> chapters) {
        return chapters.stream()
                .filter(ch -> pageIndex >= ch.getStartPage() && pageIndex <= ch.getEndPage())
                .findFirst();
    }

    public Optional<Chapter> getChapter(Long id, Long userId) throws EntityNotFoundException {
        return chapterRepo.findByIdAndUserId(id, userId);
    }

    public Chapter getChapter(Long id) throws EntityNotFoundException {
        return chapterRepo.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Chapter getChapterEagerly(Long id, Long userId) {
        return chapterRepo.findByIdAndUserIdWithSentences(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id " + id));
    }

    public List<Chapter> getAllChapters(Long pdfId, Long userId) {
        return chapterRepo.getByPdfIdAndUserId(pdfId, userId);
    }

    private Predicate<PageRange> getPageRangePredicates(int numPages) {
        return (PageRange pageRange) -> pageRange.startPage() >= 0
                && pageRange.endPage() >= 0
                && pageRange.endPage() >= pageRange.startPage()
                && pageRange.startPage() <= numPages
                && pageRange.endPage() <= numPages;
    }
}
