package com.orio.book_processing.chat.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.sentence.SentenceService;
import com.orio.book_processing.chat.dtos.ChatContextSentenceDTO;
import com.orio.book_processing.chat.dtos.PDFChatResponse;
import com.orio.book_processing.chat.models.ChatResponse;
import com.orio.book_processing.chat.models.ChatResponseContext;
import com.orio.book_processing.chat.repositories.ChatResponseContextRepository;
import com.orio.book_processing.chat.repositories.ChatResponseRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatResponseService {

    private final ChatResponseRepository chatResponseRepo;
    private final ChatResponseContextRepository chatResponseContextRepo;
    private final SentenceService sentenceService;

    public void save(Long chapterId, String query, String response, List<ChatContextSentenceDTO> context) {
        log.debug("Saving ChatResponse for query: {}", query);
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setChapterId(chapterId);
        chatResponse.setQuery(query);
        chatResponse.setContent(response);

        ChatResponse savedResponse = chatResponseRepo.saveAndFlush(chatResponse);
        log.info("ChatResponse saved with id {}", savedResponse.getId());

        // Resolve sentence entities from DTO IDs
        List<Long> sentenceIds = context.stream().map(ChatContextSentenceDTO::sentenceId).toList();
        List<Sentence> sentences = sentenceService.getSentencesByIds(sentenceIds);
        log.info("Found {} sentence links to ChatResponse {}", sentences.size(), savedResponse.getId());

        // Persist the many-to-many association between the chat response and its
        // context sentences
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
            return new PDFChatResponse(chatResponse.getQuery(), chatResponse.getId(), chatResponse.getContent(),
                    sentenceIds);
        }).toList();
    }

    public Optional<PDFChatResponse> update(Long chatResponseId, String newChatResponseBody) {
        log.info("Updating ChatResponse {}", chatResponseId);
        try {
            ChatResponse chatResponse = chatResponseRepo.getReferenceById(chatResponseId);
            chatResponse.setContent(newChatResponseBody);
            ChatResponse savedChatResponse = chatResponseRepo.saveAndFlush(chatResponse);

            // Fetch sentence IDs linked to this chat response for the DTO
            List<Long> chatResponseContextSentenceIds = chatResponseContextRepo.findByChatResponse(savedChatResponse)
                    .stream()
                    .map(ctx -> ctx.getSentence().getId()).toList();

            if (chatResponseContextSentenceIds.isEmpty()) {
                log.warn("No sentences linked to ChatResponse {} were found", chatResponseId);
                return Optional.empty();
            }

            // Create a response obj
            PDFChatResponse pdfChatResponse = new PDFChatResponse(chatResponse.getQuery(), chatResponse.getId(),
                    newChatResponseBody,
                    chatResponseContextSentenceIds);

            return Optional.of(pdfChatResponse);
        } catch (EntityNotFoundException e) {
            log.warn("ChatResponse with id {} not found", chatResponseId);
            return Optional.empty();
        }
    }

    /**
     * 
     * @param chatResponseId
     * @return true on successful deletion and false on fail
     */
    @Transactional
    public boolean deleteChatResponse(Long chatResponseId) {
        log.info("Deleting ChatResponse {}...", chatResponseId);
        if (!chatResponseRepo.existsById(chatResponseId)) {
            log.warn("Deletion of ChatResponse {} failed - not found", chatResponseId);
            return false;
        }

        Long deletedLinks = chatResponseContextRepo.deleteByChatResponse_Id(chatResponseId);
        log.info("Deleted {} ChatResponseContext links for ChatResponse {}", deletedLinks, chatResponseId);

        chatResponseRepo.deleteById(chatResponseId);
        log.info("Successfully deleted ChatResponse {}", chatResponseId);
        return true;
    }

}
