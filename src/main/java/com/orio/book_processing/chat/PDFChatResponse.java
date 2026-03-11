package com.orio.book_processing.chat;

import java.util.List;

public record PDFChatResponse(String query, String chatResponse, List<Long> contextSentencesIds) {

}
