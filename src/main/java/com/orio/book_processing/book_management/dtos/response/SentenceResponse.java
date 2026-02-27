package com.orio.book_processing.book_management.dtos.response;

import com.orio.book_processing.book_management.models.Sentence;

public record SentenceResponse(Long id, String content, int sentenceIndex, Long pdfId, Long chapterId) {

    public static SentenceResponse from(Sentence sentence) {
        return new SentenceResponse(sentence.getId(), sentence.getContent(), sentence.getSentenceIndex(),
                sentence.getPdf().getId(), sentence.getChapter().getId());
    }
}
