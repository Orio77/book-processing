package com.orio.book_processing.book_management.services.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
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

@ExtendWith(MockitoExtension.class)
class PdfUploadServiceTest {

    @Mock
    private PDFService pdfService;
    @Mock
    private ChapterService chapterService;
    @Mock
    private SentenceService sentenceService;
    @Mock
    private Tokenizer tokenizer;

    @InjectMocks
    private PdfUploadService pdfUploadService;

    private List<PageRange> ranges;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        ranges = List.of(new PageRange(1, 1));
        chapter = new Chapter();
        chapter.setId(100L);
        chapter.setTitle("c1");
    }

    @Test
    void upload_happyPath_persistsAndReturnsPdfId() throws Exception {
        byte[] pdfBytes = samplePdfBytes("First. Second.");
        MockMultipartFile file = new MockMultipartFile("file", "book.pdf", "application/pdf", pdfBytes);

        PDF created = new PDF();
        created.setTitle("book.pdf");
        when(pdfService.createPDF(any(), eq(file), eq(pdfBytes))).thenReturn(created);
        when(pdfService.savePDF(created)).thenAnswer(invocation -> {
            PDF p = invocation.getArgument(0);
            p.setId(42L);
            return p;
        });
        when(chapterService.createChapters(created, ranges)).thenReturn(List.of(chapter));
        when(chapterService.getChapterIndex(anyInt(), eq(ranges))).thenReturn(0);
        when(tokenizer.tokenize(any())).thenReturn(List.of("First.", "Second."));
        when(sentenceService.createSentences(anyList(), eq(created), eq(chapter), eq(1)))
                .thenAnswer(inv -> {
                    List<String> strs = inv.getArgument(0);
                    Sentence s = new Sentence();
                    s.setContent(String.join("", strs));
                    return List.of(s);
                });

        Long id = pdfUploadService.upload(file, ranges);

        assertEquals(42L, id);
        verify(chapterService).saveChapters(List.of(chapter));
        verify(sentenceService).saveSentences(anyList());
    }

    @Test
    void upload_getBytesFails_throwsFileContentException() throws Exception {
        MultipartFile bad = org.mockito.Mockito.mock(MultipartFile.class);
        when(bad.getBytes()).thenThrow(new IOException("bad read"));
        assertThrows(FileContentException.class, () -> pdfUploadService.upload(bad, ranges));
    }

    @Test
    void upload_invalidPdfBytes_throwsPDFLoadingException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "x.pdf", "application/pdf", new byte[] { 0, 1, 2 });
        assertThrows(PDFLoadingException.class, () -> pdfUploadService.upload(file, ranges));
    }

    @Test
    void upload_multiPage_invokesTokenizerPerPage() throws Exception {
        byte[] pdfBytes = twoPagePdf();
        MockMultipartFile file = new MockMultipartFile("file", "p.pdf", "application/pdf", pdfBytes);
        List<PageRange> twoChapters = List.of(new PageRange(1, 1), new PageRange(2, 2));
        Chapter ch1 = new Chapter();
        ch1.setId(1L);
        Chapter ch2 = new Chapter();
        ch2.setId(2L);
        List<Chapter> chapters = List.of(ch1, ch2);

        PDF created = new PDF();
        when(pdfService.createPDF(any(), any(), any())).thenReturn(created);
        when(pdfService.savePDF(created)).thenAnswer(invocation -> {
            PDF p = invocation.getArgument(0);
            p.setId(7L);
            return p;
        });
        when(chapterService.createChapters(created, twoChapters)).thenReturn(chapters);
        when(chapterService.getChapterIndex(1, twoChapters)).thenReturn(0);
        when(chapterService.getChapterIndex(2, twoChapters)).thenReturn(1);
        when(tokenizer.tokenize(any())).thenReturn(List.of("x"));
        when(sentenceService.createSentences(anyList(), any(), any(), anyInt()))
                .thenAnswer(inv -> List.of(new Sentence()));

        Long id = pdfUploadService.upload(file, twoChapters);

        assertEquals(7L, id);
        verify(tokenizer, times(2)).tokenize(any());
    }

    private static byte[] samplePdfBytes(String text) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                cs.showText(text);
                cs.endText();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private static byte[] twoPagePdf() throws Exception {
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < 2; i++) {
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.beginText();
                    cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    cs.newLineAtOffset(50, 700);
                    cs.showText("Page " + (i + 1) + " content.");
                    cs.endText();
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            doc.save(baos);
            return baos.toByteArray();
        }
    }
}
