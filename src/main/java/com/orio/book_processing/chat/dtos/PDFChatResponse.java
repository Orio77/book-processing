package com.orio.book_processing.chat.dtos;

import java.util.List;

/**
 * API response payload for a chapter chat interaction.
 */
public record PDFChatResponse(String query, Long chatResponseId, String chatResponse, List<Long> contextSentencesIds) {

}
