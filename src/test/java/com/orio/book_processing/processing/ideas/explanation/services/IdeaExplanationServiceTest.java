package com.orio.book_processing.processing.ideas.explanation.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.PDF;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.processing.ideas.explanation.models.IdeaExplanation;
import com.orio.book_processing.processing.ideas.explanation.repositories.IdeaExplanationRepository;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;

@ExtendWith(MockitoExtension.class)
class IdeaExplanationServiceTest {

    @Mock
    private IdeaExplanationRepository ideaExplanationRepo;
    @Mock
    private IdeaRepository ideaRepo;
    @Mock
    private IdeaExplanationGenerationService ideaExplanationGenerationService;

    @InjectMocks
    private IdeaExplanationService service;

    @Test
    void getExplanationsForIdea_wrapsRepositoryListInOptional() {
        IdeaExplanation one = new IdeaExplanation();
        when(ideaExplanationRepo.findAllByIdeaId(5L)).thenReturn(List.of(one));

        Optional<List<IdeaExplanation>> out = service.getExplanationsForIdea(5L);

        assertTrue(out.isPresent());
        assertEquals(1, out.get().size());
    }

    @Test
    void getIdeaExplanation_whenMissing_returnsEmpty() {
        when(ideaExplanationRepo.findById(9L)).thenReturn(Optional.empty());

        assertTrue(service.getIdeaExplanation(9L).isEmpty());
    }

    @Test
    void getIdeaExplanation_whenPresent_returnsExplanation() {
        IdeaExplanation ex = new IdeaExplanation();
        ex.setId(2L);
        when(ideaExplanationRepo.findById(2L)).thenReturn(Optional.of(ex));

        assertTrue(service.getIdeaExplanation(2L).isPresent());
    }

    @Test
    void update_whenContentBlank_returnsEmptyWithoutSave() {
        assertTrue(service.update(1L, "   ").isEmpty());
        verify(ideaExplanationRepo, never()).findById(any());
    }

    @Test
    void update_whenExplanationMissing_returnsEmpty() {
        when(ideaExplanationRepo.findById(3L)).thenReturn(Optional.empty());

        assertTrue(service.update(3L, "new text").isEmpty());
    }

    @Test
    void update_whenFound_persistsNewText() {
        IdeaExplanation ex = new IdeaExplanation();
        ex.setId(4L);
        ex.setText("old");
        when(ideaExplanationRepo.findById(4L)).thenReturn(Optional.of(ex));
        when(ideaExplanationRepo.saveAndFlush(ex)).thenReturn(ex);

        Optional<IdeaExplanation> out = service.update(4L, "fresh");

        assertTrue(out.isPresent());
        assertEquals("fresh", ex.getText());
    }

    @Test
    void delete_whenRowMissing_returnsFalse() {
        when(ideaExplanationRepo.existsById(8L)).thenReturn(false);

        assertFalse(service.delete(8L));
        verify(ideaExplanationRepo, never()).deleteById(any());
    }

    @Test
    void delete_whenDeleteThrows_returnsFalse() {
        when(ideaExplanationRepo.existsById(8L)).thenReturn(true);
        doThrow(new RuntimeException("db")).when(ideaExplanationRepo).deleteById(8L);

        assertFalse(service.delete(8L));
    }

    @Test
    void delete_whenSuccessful_returnsTrue() {
        when(ideaExplanationRepo.existsById(8L)).thenReturn(true);

        assertTrue(service.delete(8L));
        verify(ideaExplanationRepo).deleteById(8L);
    }

    @Test
    void createExplanation_whenContentBlank_returnsEmpty() {
        assertTrue(service.createExplanation(1L, null).isEmpty());
    }

    @Test
    void createExplanation_whenIdeaMissing_returnsEmpty() {
        when(ideaRepo.findById(1L)).thenReturn(Optional.empty());

        assertTrue(service.createExplanation(1L, "body").isEmpty());
    }

    @Test
    void createExplanation_generatesTextAndSaves() {
        PDF pdf = new PDF();
        pdf.setId(1L);
        pdf.setTitle("t");
        pdf.setTotalPages(1);
        pdf.setContent(new byte[] { 1 });

        Chapter chapter = new Chapter();
        chapter.setId(10L);
        chapter.setStartPage(1);
        chapter.setEndPage(1);
        chapter.setPdf(pdf);

        Sentence sentence = new Sentence();
        sentence.setId(100L);
        sentence.setContent("abc");
        sentence.setSentenceIndex(0);
        sentence.setPageNum(1);
        sentence.setPdf(pdf);
        sentence.setChapter(chapter);
        chapter.getSentences().add(sentence);

        Idea idea = new Idea();
        idea.setId(7L);
        idea.setTitle("T");
        IdeaArgument arg = new IdeaArgument();
        arg.setText("arg");
        arg.setIdea(idea);
        idea.getArguments().add(arg);

        IdeaSentence link = new IdeaSentence(idea, sentence);
        idea.getSentences().add(link);

        when(ideaRepo.findById(7L)).thenReturn(Optional.of(idea));
        when(ideaExplanationGenerationService.generateIdeaExplanation(
                eq("T"), eq(List.of("arg")), anyString())).thenReturn("llm out");

        IdeaExplanation saved = new IdeaExplanation();
        saved.setId(200L);
        when(ideaExplanationRepo.save(any(IdeaExplanation.class))).thenAnswer(inv -> {
            IdeaExplanation e = inv.getArgument(0);
            e.setId(200L);
            return e;
        });

        Optional<IdeaExplanation> out = service.createExplanation(7L, "non-blank");

        assertTrue(out.isPresent());
        assertEquals(200L, out.get().getId());
        assertEquals("llm out", out.get().getText());
        verify(ideaExplanationGenerationService).generateIdeaExplanation(eq("T"), eq(List.of("arg")), anyString());
    }

    @Test
    void createExplanations_mapsEachIdeaThroughCreateExplanation() {
        Idea idea = new Idea();
        idea.setId(3L);
        idea.setTitle("only title");
        when(ideaRepo.findDistinctBySentencesSentenceChapterId(99L)).thenReturn(List.of(idea));

        assertEquals(1, service.createExplanations(99L).size());
    }
}
