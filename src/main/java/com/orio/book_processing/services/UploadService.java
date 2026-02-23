package com.orio.book_processing.services;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService implements IUploadService {

    private final PDFService pdfService;
    private final ChapterService chapterService;
    private final SentenceService sentenceService;

    private final ITokenizer tokenizer;

    @Override
    public Long upload(MultipartFile file, List<PageRange> chapterPageRanges) throws IOException {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            PDF resPdf = pdfService.savePDF(doc, file);

            List<Chapter> chapters = chapterService.createChapters(resPdf, chapterPageRanges);
            chapterService.saveChapters(chapters);

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String pageContent = stripper.getText(doc);

                int chapterIndex = chapterService.getChapterIndex(i, chapterPageRanges);
                Chapter chapter = chapters.get(chapterIndex);

                List<String> strSentences = tokenizer.tokenize(pageContent);
                List<Sentence> sentences = sentenceService.createSentences(strSentences, resPdf, chapter, i);
                sentenceService.saveSentences(sentences);
            }

            return resPdf.getId();
        }
    }

}
