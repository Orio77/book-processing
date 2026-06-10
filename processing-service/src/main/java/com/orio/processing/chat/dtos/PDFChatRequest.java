package com.orio.processing.chat.dtos;

import java.util.List;

/**
 * CHAT job payload. Mirrors the JSON shape produced by books-service.
 */
public record PDFChatRequest(Long chapterId, String query, List<ChatContextSentenceDTO> context, Long userId) {

}
