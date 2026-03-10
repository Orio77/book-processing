package com.orio.book_processing.chat;

import java.util.List;

import com.orio.book_processing.processing.ideas.extraction.models.dtos.response.SentenceDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PDFChatRequest {

    private Long chapterId;
    private String query;
    private List<SentenceDTO> context;
}
