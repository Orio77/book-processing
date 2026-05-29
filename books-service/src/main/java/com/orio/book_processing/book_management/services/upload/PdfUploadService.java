package com.orio.book_processing.book_management.services.upload;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfUploadService implements UploadService {

    private final PDFService pdfService;
    private final ChapterService chapterService;
    private final SentenceService sentenceService;

    private final Tokenizer tokenizer;

    @Override
    @Transactional
    public Long upload(MultipartFile file, List<PageRange> chapterPageRanges, Long userId)
            throws PDFLoadingException, FileContentException {

        log.info("Uploading a pdf...");
        byte[] fileBytes = getFileBytes(file);
        log.info("Pdf bytes read successfully");

        // Parse the PDF, create chapter + sentence entities, and persist everything
        try (PDDocument doc = Loader.loadPDF(fileBytes)) {
            PDF resPdf = createAndSavePdf(file, userId, fileBytes, doc);

            List<Chapter> chapters = createAndSaveChapters(chapterPageRanges, userId, resPdf);

            PDFTextStripper stripper = new PDFTextStripper();

            log.info("Creating sentences...");
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                createAndSaveSentences(userId, doc, resPdf, chapters, stripper, i);
            }

            log.info("All sentences for {} pages created successfully", doc.getNumberOfPages());
            return resPdf.getId();

        } catch (IOException e) {
            log.warn("Exception while uploading a PDF, stopping...");
            throw new PDFLoadingException(e.getMessage(), e.getCause());
        }

    }

    private byte[] getFileBytes(MultipartFile file) throws FileContentException {
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            log.warn("File bytes of the provided pdf are corrupted, stopping...");
            throw new FileContentException(e.getMessage(), e.getCause());
        }
        return fileBytes;
    }

    private PDF createAndSavePdf(MultipartFile file, Long userId, byte[] fileBytes, PDDocument doc) {
        PDF resPdf = pdfService.createPDF(doc, file, fileBytes);
        log.info("PDF object created successfully: {}", resPdf.getTitle());
        resPdf = pdfService.savePDFForUser(resPdf, userId);
        return resPdf;
    }

    private List<Chapter> createAndSaveChapters(List<PageRange> chapterPageRanges, Long userId, PDF resPdf) {
        log.info("Creating chapters...");
        List<Chapter> chapters = chapterService.createChapters(resPdf, chapterPageRanges);
        chapterService.saveChapters(chapters, userId);
        log.info("Saved {} chapters", chapters.size());
        return chapters;
    }

    private void createAndSaveSentences(Long userId, PDDocument doc, PDF resPdf, List<Chapter> chapters,
            PDFTextStripper stripper,
            int i) throws IOException {
        Optional<Chapter> maybeChapter = chapterService.getChapterForPage(i, chapters);
        if (maybeChapter.isEmpty()) {
            return;
        }
        Chapter chapter = maybeChapter.get();

        stripper.setStartPage(i);
        stripper.setEndPage(i);
        String pageContent = stripper.getText(doc);

        List<String> strSentences = tokenizer.tokenize(pageContent);
        List<Sentence> sentences = sentenceService.createSentences(strSentences, resPdf, chapter, i);
        sentenceService.saveSentences(sentences, userId);
        log.debug("{} sentences for page {} created successfully", sentences.size(), i);
    }

}