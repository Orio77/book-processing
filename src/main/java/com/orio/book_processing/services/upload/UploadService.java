package com.orio.book_processing.services.upload;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.orio.book_processing.dtos.request.PageRange;
import com.orio.book_processing.exceptions.FileContentException;
import com.orio.book_processing.exceptions.PDFLoadingException;
import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;
import com.orio.book_processing.services.chapter.ChapterService;
import com.orio.book_processing.services.pdf.PDFService;
import com.orio.book_processing.services.sentence.SentenceService;
import com.orio.book_processing.services.tokenizer.ITokenizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadService implements IUploadService {

    private final PDFService pdfService;
    private final ChapterService chapterService;
    private final SentenceService sentenceService;

    private final ITokenizer tokenizer;

    @Override
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
            PDF resPdf = pdfService.savePDF(doc, file, fileBytes);

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

        } catch (IOException e) {
            throw new PDFLoadingException(e.getMessage(), e.getCause());
        }

    }
}