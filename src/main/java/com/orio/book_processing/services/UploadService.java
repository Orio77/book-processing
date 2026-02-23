package com.orio.book_processing.services;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;
import com.orio.book_processing.repositories.ChapterRepository;
import com.orio.book_processing.repositories.PDFRepository;
import com.orio.book_processing.repositories.SentenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService implements IUploadService {

    private final PDFRepository pdfRepo;
    private final ChapterRepository chapterRepo;
    private final SentenceRepository sentenceRepo;

    public PDF savePDF(PDDocument doc, MultipartFile file) throws IOException {
        PDF pdf = new PDF();
        String title = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) ? "unknown"
                : file.getOriginalFilename();

        pdf.setTitle(title);
        pdf.setTotalPages(doc.getNumberOfPages());
        pdf.setContent(file.getBytes());

        return pdfRepo.saveAndFlush(pdf);
    }

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

    public List<String> getStrSentences(String str) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(str);
        List<String> sentences = new ArrayList<>();

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(str.substring(start, end));
        }
        return sentences;
    }

    public List<Sentence> createSentences(List<String> strSentences, PDF pdf, Chapter chapter, int pageNum) {
        return IntStream.range(0, strSentences.size()).mapToObj(idx -> {
            String strSentence = strSentences.get(idx);
            Sentence sentence = new Sentence();
            sentence.setPdf(pdf);
            sentence.setChapter(chapter);
            sentence.setContent(strSentence);
            sentence.setPageNum(pageNum);
            sentence.setSentenceIndex(idx);
            return sentence;
        }).toList();
    }

    public void saveSentences(List<Sentence> sentences) {
        sentenceRepo.saveAll(sentences);
    }
}
