package com.orio.book_processing.services;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;
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
public class UploadService {

    private final PDFRepository pdfRepo;
    private final ChapterRepository chapterRepo;
    private final SentenceRepository sentenceRepo;

    public Long upload(MultipartFile file, List<PageRange> chapterPageRanges) {

        try {
            PDDocument doc = Loader.loadPDF(file.getBytes());

            PDF resPdf = savePDF(doc, file);

            List<Chapter> chapterObjects = createChapterObjects(resPdf, chapterPageRanges);
            saveChapterObjects(chapterObjects);

            PDPageTree pages = doc.getPages();
            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i <= pages.getCount(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageContent = stripper.getText(doc);

                List<String> sentences = getSentences(pageContent);
                List<Sentence> sentenceObjs = createSentenceObjects(sentences, resPdf, null, i);
                saveSentenceObjects(sentenceObjs);
            }

            return resPdf.getId();

        } catch (IOException e) {
            return null;
        }
    }

    public PDF savePDF(PDDocument doc, MultipartFile file) {

        try {
            PDF pdf = new PDF();
            String title = (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) ? "unknown"
                    : file.getOriginalFilename();

            pdf.setTitle(title);
            pdf.setTotalPages(doc.getNumberOfPages());
            pdf.setContent(file.getBytes());

            return pdfRepo.saveAndFlush(pdf);

        } catch (Exception e) {
            return null;
        }
    }

    public List<Chapter> createChapterObjects(PDF pdf, List<PageRange> chapterPageRanges) {

        return chapterPageRanges.stream().map((PageRange pageRange) -> {
            try {
                Chapter chapter = new Chapter();
                chapter.setPdf(pdf);
                chapter.setStartPage(pageRange.startPage());
                chapter.setEndPage(pageRange.endPage());

                return chapter;
            } catch (Exception e) {
                return null;
            }
        }).toList();
    }

    public void saveChapterObjects(List<Chapter> chapterObjects) {
        chapterRepo.saveAll(chapterObjects);
    }

    public List<String> getSentences(String str) {
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        List<String> sentences = new ArrayList<>();

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            sentences.add(str.substring(start, end));
        }
        return sentences;
    }

    public List<Sentence> createSentenceObjects(List<String> strSentences, PDF pdf, Chapter chapter, int pageNum) {
        AtomicInteger sentenceIdx = new AtomicInteger(0);

        return strSentences.stream().map(strSentence -> {
            Sentence sentence = new Sentence();
            sentence.setPdf(pdf);
            sentence.setChapter(chapter);
            sentence.setContent(strSentence);
            sentence.setPageNum(pageNum);
            sentence.setSentenceIndex(sentenceIdx.getAndIncrement());
            return sentence;
        }).toList();
    }

    public void saveSentenceObjects(List<Sentence> sentenceObjs) {
        sentenceRepo.saveAll(sentenceObjs);
    }
}
