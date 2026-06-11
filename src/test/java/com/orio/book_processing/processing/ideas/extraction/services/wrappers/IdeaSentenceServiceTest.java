package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

@ExtendWith(MockitoExtension.class)
class IdeaSentenceServiceTest {

    @Mock
    private IdeaSentenceRepository ideaSentenceRepo;

    @InjectMocks
    private IdeaSentenceService ideaSentenceService;

    @Test
    void getSentencesByIdeaId_mapsLinkedSentencesInOrder() {
        Idea idea = new Idea();
        idea.setId(1L);
        Sentence a = new Sentence();
        a.setId(10L);
        Sentence b = new Sentence();
        b.setId(11L);
        when(ideaSentenceRepo.findAllByIdeaId(1L)).thenReturn(List.of(
                new IdeaSentence(idea, a),
                new IdeaSentence(idea, b)));

        List<Sentence> out = ideaSentenceService.getSentencesByIdeaId(1L);

        assertEquals(List.of(10L, 11L), out.stream().map(Sentence::getId).toList());
    }
}
