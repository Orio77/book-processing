package com.orio.book_processing.chat;

import java.util.List;

import org.springframework.stereotype.Service;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.processing.ideas.extraction.models.dtos.response.SentenceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatResponseService {

    private final ChatResponseRepository chatResponseRepo;
    private final ChatResponseContextRepository chatResponseContextRepo;
    private final SentenceService sentenceService;

    public void save(Long chapterId, String query, String response, List<SentenceDTO> context) {
        log.debug("Saving ChatResponse for query: {}", query);
        // create ChatResponse obj
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setChapterId(chapterId);
        chatResponse.setQuery(query);
        chatResponse.setContent(response);

        // save ChatResponse obj
        ChatResponse savedResponse = chatResponseRepo.saveAndFlush(chatResponse);
        log.info("ChatResponse saved with id {}", savedResponse.getId());

        // parse Sentence objs
        List<Long> sentenceIds = context.stream().map(SentenceDTO::sentenceId).toList();
        List<Sentence> sentences = sentenceService.getSentencesByIds(sentenceIds);
        log.info("Found {} sentence links to ChatResponse {}", sentences.size(), savedResponse.getId());

        // save response <-> sentence links
        List<ChatResponseContext> chatResponseContexts = sentences.stream().map(sentence -> {
            ChatResponseContext chatResponseContext = new ChatResponseContext();
            chatResponseContext.setChatResponse(savedResponse);
            chatResponseContext.setSentence(sentence);
            return chatResponseContext;
        }).toList();
        chatResponseContextRepo.saveAll(chatResponseContexts);
        log.info("Saved {} links to ChatResponse {}", chatResponseContexts.size(), savedResponse.getId());
    }

    public List<PDFChatResponse> getChatResponsesForChapter(Long chapterId) {
        log.info("Parsing chat responses for chapter {}...", chapterId);
        List<ChatResponse> chatResponses = chatResponseRepo.getByChapterId(chapterId);
        log.info("Found {} chat responses for chapter {}", chatResponses.size(), chapterId);

        log.info("Parsing ChatResponse <-> Sentence links for chapter {}", chapterId);
        List<ChatResponseContext> contexts = chatResponseContextRepo.findByChatResponseIn(chatResponses);
        log.info("Found {} links for chapter {}", contexts.size(), chapterId);

        return chatResponses.stream().map(chatResponse -> {
            List<Long> sentenceIds = contexts.stream()
                    .filter(ctx -> ctx.getChatResponse().getId().equals(chatResponse.getId()))
                    .map(ctx -> ctx.getSentence().getId())
                    .toList();
            return new PDFChatResponse(chatResponse.getQuery(), chatResponse.getContent(), sentenceIds);
        }).toList();
    }

}
