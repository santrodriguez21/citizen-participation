package platform.service;

import entities.domain.Comment;
import entities.domain.Proposal;
import exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import platform.repository.ProposalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ModeratorServiceTest {

    @MockBean
    private ProposalRepository proposalRepository;

    @Autowired
    private ModeratorService moderatorService;

    @Test
    void testDeleteComment() {
        String proposalId = "1";
        String commentId = "2";

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setDescription("A comment");

        Proposal proposal = new Proposal();
        proposal.setId(proposalId);
        proposal.setComments(new ArrayList<>(List.of(comment)));

        when(proposalRepository.findById(proposalId)).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(proposal)).thenReturn(proposal);

        moderatorService.deleteComment(proposalId, commentId);

        assertTrue(proposal.getComments().isEmpty());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void testDeleteProposalNotFound() {
        String proposalId = "1";
        String commentId = "2";

        when(proposalRepository.findById(proposalId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> moderatorService.deleteComment(proposalId, commentId));
        assertEquals("Proposal not found", exception.getMessage());
    }
}
