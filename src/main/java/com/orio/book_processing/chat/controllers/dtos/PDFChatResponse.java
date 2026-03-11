package com.orio.book_processing.chat.controllers.dtos;

import java.util.List;

public record PDFChatResponse(String query, String chatResponse, List<Long> contextSentencesIds) {

}
