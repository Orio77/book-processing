package com.orio.book_processing.services.chapter;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.repositories.ChapterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepo;

    public List<Chapter> createChapters(PDF pdf, List<PageRange> chapterPageRanges) {

        return chapterPageRanges.stream().map((PageRange pageRange) -> {
            Chapter chapter = new Chapter();
            chapter.setPdf(pdf);
            chapter.setStartPage(pageRange.startPage());
            chapter.setEndPage(pageRange.endPage());
            return chapter;
        }).toList();
    }

    public void saveChapters(List<Chapter> chapters) {
        chapterRepo.saveAll(chapters);
    }

    public int getChapterIndex(int pageIndex, List<PageRange> chapterPageRanges) {
        // Find the range wihtin which the page is placed
        for (int i = 0; i < chapterPageRanges.size(); i++) {
            if (pageIndex >= chapterPageRanges.get(i).startPage() && pageIndex <= chapterPageRanges.get(i).endPage()) {
                return i;
            }
        }

        // When page isn't placed within any of chapter page ranges
        throw new IllegalArgumentException("Invalid page range");
    }
}
