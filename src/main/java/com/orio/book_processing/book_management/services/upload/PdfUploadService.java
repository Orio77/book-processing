package com.orio.book_processing.book_management.services.upload;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.exceptions.FileContentException;
import com.orio.book_processing.book_management.exceptions.PDFLoadingException;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.book_management.services.pdf.PDFService;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.book_management.services.tokenizer.Tokenizer;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfUploadService implements UploadService {

    private final PDFService pdfService;
    private final ChapterService chapterService;
    private final SentenceService sentenceService;

    private final Tokenizer tokenizer;

    @Override
    @Transactional
    public Long upload(MultipartFile file, List<PageRange> chapterPageRanges)
            throws PDFLoadingException, FileContentException {

        // Read file bytes
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new FileContentException(e.getMessage(), e.getCause());
        }

        // proceed with upload logic
        try (PDDocument doc = Loader.loadPDF(fileBytes)) {
            PDF resPdf = pdfService.createPDF(doc, file, fileBytes);
            resPdf = pdfService.savePDF(resPdf);

            List<Chapter> chapters = chapterService.createChapters(resPdf, chapterPageRanges);
            chapterService.saveChapters(chapters);

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
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

        } catch (IOException e) {
            throw new PDFLoadingException(e.getMessage(), e.getCause());
        }

    }
}