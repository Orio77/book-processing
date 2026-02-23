package com.orio.book_processing.services;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.orio.book_processing.models.Chapter;
import com.orio.book_processing.models.PDF;
import com.orio.book_processing.models.Sentence;
import com.orio.book_processing.repositories.SentenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SentenceService {

    private final SentenceRepository sentenceRepo;

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
