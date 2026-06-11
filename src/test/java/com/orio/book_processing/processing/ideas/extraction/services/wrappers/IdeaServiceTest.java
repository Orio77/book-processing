package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.book_management.models.Chapter;
import com.orio.book_processing.book_management.models.Sentence;
import com.orio.book_processing.book_management.services.chapter.ChapterService;
import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaWithSentences;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaSentence;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaRepository;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaSentenceRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class IdeaServiceTest {

    @Mock
    private IdeaRepository ideaRepo;
    @Mock
    private IdeaSentenceService ideaSentenceService;
    @Mock
    private ChapterService chapterService;
    @Mock
    private IdeaSentenceRepository ideaSentenceRepo;

    @InjectMocks
    private IdeaService ideaService;

    @Test
    void getIdea_whenReferenceMissing_returnsEmpty() {
        when(ideaRepo.getReferenceById(9L)).thenThrow(new EntityNotFoundException());

        assertTrue(ideaService.getIdea(9L).isEmpty());
    }

    @Test
    void getIdea_whenFound_mapsDtoAndSentences() {
        Idea idea = new Idea();
        idea.setId(2L);
        idea.setTitle("Hi");
        Sentence s = new Sentence();
        s.setId(20L);
        s.setContent("body");

        when(ideaRepo.getReferenceById(2L)).thenReturn(idea);
        when(ideaSentenceService.getSentencesByIdeaId(2L)).thenReturn(List.of(s));

        Optional<IdeaWithSentences> out = ideaService.getIdea(2L);

        assertTrue(out.isPresent());
        assertEquals("Hi", out.get().idea().title());
        assertEquals(1, out.get().sentences().size());
        assertEquals(20L, out.get().sentences().getFirst().id());
    }

    @Test
    void getIdeasByChapter_groupsLinksByIdea() {
        Chapter chapter = new Chapter();
        chapter.setId(1L);
        Sentence s = new Sentence();
        s.setId(100L);
        chapter.setSentences(new ArrayList<>(List.of(s)));

        Idea idea = new Idea();
        idea.setId(3L);
        idea.setTitle("Grouped");
        IdeaSentence link = new IdeaSentence(idea, s);

        when(chapterService.getChapter(1L)).thenReturn(chapter);
        when(ideaSentenceRepo.findAllBySentence_IdIn(List.of(100L))).thenReturn(List.of(link));

        Optional<List<IdeaWithSentences>> out = ideaService.getIdeasByChapter(1L);

        assertTrue(out.isPresent());
        assertEquals(1, out.get().size());
        assertEquals(3L, out.get().getFirst().idea().id());
        assertEquals(1, out.get().getFirst().sentences().size());
    }

    @Test
    void deleteIdea_reportsWhetherRowStillExists() {
        when(ideaRepo.existsById(4L)).thenReturn(false);

        Optional<Boolean> out = ideaService.deleteIdea(4L);

        assertTrue(out.isPresent());
        assertTrue(out.get());
        verify(ideaRepo).deleteById(4L);
    }

    @Test
    void deleteIdea_whenStillPresentAfterDelete_returnsFalse() {
        when(ideaRepo.existsById(4L)).thenReturn(true);

        Optional<Boolean> out = ideaService.deleteIdea(4L);

        assertTrue(out.isPresent());
        assertFalse(out.get());
    }
}
