package com.orio.book_processing.chat.dtos;

import java.util.List;

public record PDFChatResponse(String query, Long chatResponseId, String chatResponse, List<Long> contextSentencesIds) {

}
