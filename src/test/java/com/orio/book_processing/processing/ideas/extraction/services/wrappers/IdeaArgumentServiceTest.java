package com.orio.book_processing.processing.ideas.extraction.services.wrappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.orio.book_processing.processing.ideas.extraction.dtos.IdeaArgumentDTO;
import com.orio.book_processing.processing.ideas.extraction.models.Idea;
import com.orio.book_processing.processing.ideas.extraction.models.IdeaArgument;
import com.orio.book_processing.processing.ideas.extraction.repositories.IdeaArgumentRepository;

@ExtendWith(MockitoExtension.class)
class IdeaArgumentServiceTest {

    @Mock
    private IdeaArgumentRepository ideaArgumentRepo;

    @InjectMocks
    private IdeaArgumentService ideaArgumentService;

    @Test
    void getIdeaArgumentsForIdea_returnsDtosForEachRow() {
        Idea idea = new Idea();
        idea.setId(5L);
        IdeaArgument arg = new IdeaArgument();
        arg.setId(50L);
        arg.setText("claim");
        arg.setIdea(idea);
        when(ideaArgumentRepo.findAllByIdea_Id(5L)).thenReturn(List.of(arg));

        var out = ideaArgumentService.getIdeaArgumentsForIdea(5L);

        assertTrue(out.isPresent());
        List<IdeaArgumentDTO> list = out.get();
        assertEquals(1, list.size());
        assertEquals(50L, list.getFirst().id());
        assertEquals("claim", list.getFirst().text());
    }
}
