package com.orio.book_processing.services;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;

public interface IUploadService {

    default Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws IOException {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            PDF resPdf = savePDF(doc, file);

            List<Chapter> chapters = createChapters(resPdf, chapterPageRanges);
            saveChapters(chapters);

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageContent = stripper.getText(doc);

                int chapterIndex = getChapterIndex(i, chapterPageRanges);
                Chapter chapter = chapters.get(chapterIndex);

                List<String> strSentences = getStrSentences(pageContent);
                List<Sentence> sentences = createSentences(strSentences, resPdf, chapter, i);
                saveSentences(sentences);
            }

            return resPdf.getId();
        }
    }

    void saveSentences(List<Sentence> sentences);

    List<Sentence> createSentences(List<String> strSentences, PDF resPdf, Chapter chapter, int i);

    List<String> getStrSentences(String pageContent);

    void saveChapters(List<Chapter> chapters);

    List<Chapter> createChapters(PDF resPdf, List<PageRange> chapterPageRanges);

    int getChapterIndex(int i, List<PageRange> chapterPageRanges);

    PDF savePDF(PDDocument doc, MultipartFile file) throws IOException;

}
