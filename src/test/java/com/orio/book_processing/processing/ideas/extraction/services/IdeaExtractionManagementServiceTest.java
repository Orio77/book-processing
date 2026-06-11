package com.orio.book_processing.processing.ideas.extraction.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaExtractionAiResponse.IdeaRequest;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;
import com.orio.book_processing.book_management.services.sentence.SentenceService;

@ExtendWith(MockitoExtension.class)
class IdeaExtractionManagementServiceTest {

    @Mock
    private SentenceService sentenceService;
    @Mock
    private IdeaRepository ideaRepo;
    @Mock
    private BasicIdeaExtractionService extractionService;

    @InjectMocks
    private IdeaExtractionManagementService managementService;

    @Test
    void extractIdeas_persistsIdeasAndFiltersUnknownSentenceIds() {
        Sentence s1 = new Sentence();
        s1.setId(10L);
        s1.setContent("a");

        IdeaExtractionAiResponse llmOut = new IdeaExtractionAiResponse(List.of(
                new IdeaRequest("Title one", List.of("arg1"), List.of(10L, 999L))));

        when(sentenceService.getSentencesByChapterId(1L)).thenReturn(List.of(s1));
        when(extractionService.getIdeas(List.of(s1))).thenReturn(llmOut);

        IdeaExtractionAiResponse result = managementService.extractIdeas(1L);

        assertEquals(1, result.ideaContainers().size());
        assertEquals("Title one", result.ideaContainers().getFirst().ideaTitle());

        ArgumentCaptor<Idea> ideaCaptor = ArgumentCaptor.forClass(Idea.class);
        verify(ideaRepo).save(ideaCaptor.capture());
        Idea saved = ideaCaptor.getValue();
        assertEquals("Title one", saved.getTitle());
        assertEquals(1, saved.getArguments().size());
        assertEquals("arg1", saved.getArguments().getFirst().getText());
        assertEquals(1, saved.getSentences().size());
        assertEquals(10L, saved.getSentences().getFirst().getSentence().getId());
    }

    @Test
    void extractIdeas_whenLlmReturnsNoSentenceLinks_stillSavesIdea() {
        Sentence s1 = new Sentence();
        s1.setId(1L);
        s1.setContent("x");
        IdeaExtractionAiResponse llmOut = new IdeaExtractionAiResponse(
                List.of(new IdeaRequest("Only title", List.of(), List.of())));

        when(sentenceService.getSentencesByChapterId(2L)).thenReturn(List.of(s1));
        when(extractionService.getIdeas(any())).thenReturn(llmOut);

        managementService.extractIdeas(2L);

        ArgumentCaptor<Idea> ideaCaptor = ArgumentCaptor.forClass(Idea.class);
        verify(ideaRepo).save(ideaCaptor.capture());
        assertTrue(ideaCaptor.getValue().getSentences().isEmpty());
    }
}
