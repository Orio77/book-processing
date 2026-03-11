package com.orio.book_processing.book_management.services.sentence;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.dtos.request.PageRange;
import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.repositories.SentenceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SentenceService {

    private final SentenceRepository sentenceRepo;

    public List<Sentence> createSentences(List<String> strSentences, PDF pdf, Chapter chapter, int pageNum) {
        List<Sentence> sentences = IntStream.range(0, strSentences.size()).mapToObj(idx -> {
            String strSentence = strSentences.get(idx);
            Sentence sentence = new Sentence();
            sentence.setPdf(pdf);
            sentence.setChapter(chapter);
            sentence.setContent(strSentence);
            sentence.setPageNum(pageNum);
            sentence.setSentenceIndex(idx);
            return sentence;
        }).toList();
        log.info("Created {} sentences out of {} string sentences for pdf \"{}\", chapter \"{}\" and page {}",
                sentences.size(), strSentences.size(), pdf.getTitle(), chapter.getTitle(), pageNum);
        return sentences;
    }

    public void saveSentences(List<Sentence> sentences) {
        log.info("Saving {} sentences", sentences.size());
        sentenceRepo.saveAll(sentences);
    }

    public List<Sentence> getSentencesByIds(List<Long> sentenceIds) {
        return sentenceRepo.findAllById(sentenceIds);
    }

    public List<Sentence> getSentencesInRange(PageRange pageRange, Long pdfId) {
        return sentenceRepo.getByPageNumBetweenAndPdfId(pageRange.startPage(), pageRange.endPage(), pdfId);
    }

    public List<List<Sentence>> getSentencesInRanges(List<PageRange> ranges, Long pdfId) {
        List<Sentence> all = sentenceRepo.getByPdfId(pdfId);
        return ranges.stream()
                .map(range -> all.stream()
                        .filter(s -> s.getPageNum() >= range.startPage() && s.getPageNum() <= range.endPage())
                        .toList())
                .toList();
    }

    public List<Sentence> getSentencesByChapterId(Long chapterId) {
        return sentenceRepo.getByChapterId(chapterId);
    }

}
