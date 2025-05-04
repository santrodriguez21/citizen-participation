package platform.service;

import entities.domain.Comment;
import entities.domain.Proposal;
import entities.domain.Vote;
import exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import platform.repository.ProposalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProposalServiceTest {

    @MockBean
    private ProposalRepository proposalRepository;

    @Autowired
    private ProposalService proposalService;

    @Test
    void testCreateProposal() {
        Proposal proposal = buildGenericProposal();

        // Simulamos el contexto de seguridad
        String userDocument = "123456";
        setAuthentication(userDocument, "password", "ROLE_USER");

        when(proposalRepository.save(any(Proposal.class))).thenReturn(proposal);

        Proposal result = proposalService.create(proposal);

        assertNotNull(result);
        assertEquals(userDocument, result.getAuthorDocument());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void testCreateProposalWithNullFields() {
        Proposal proposal = new Proposal();
        proposal.setTitle(null);  // Campo nulo

        String userDocument = "123456";
        setAuthentication(userDocument, "password", "ROLE_USER");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> proposalService.create(proposal));
        assertEquals("Proposal fields can not be empty", exception.getMessage());
    }

    @Test
    void testDeleteProposalByNonAuthor() {
        Proposal proposal = buildGenericProposal();

        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));

        // Simulamos que el usuario autenticado no es el autor
        String userDocument = "49359161";
        setAuthentication(userDocument, "password", "ROLE_USER");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> proposalService.delete(proposal.getId()));
        assertEquals("Only the author can delete the proposal", exception.getMessage());
    }

    @Test
    void testDeleteProposalByAuthor() {
        Proposal proposal = buildGenericProposal();

        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));

        // Simulamos que el usuario autenticado es el autor
        String userDocument = "123456";
        setAuthentication(userDocument, "password", "ROLE_USER");

        proposalService.delete(proposal.getId());

        verify(proposalRepository).deleteById(proposal.getId());
    }

    @Test
    void testDeleteProposalNotFound() {
        String proposalId = "1";

        when(proposalRepository.findById(proposalId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> proposalService.delete(proposalId));
        assertEquals("Proposal not found", exception.getMessage());
    }

    @Test
    void testCommentOnProposal() {
        Proposal proposal = buildGenericProposal();
        proposal.setComments(new ArrayList<>());

        Comment comment = new Comment();
        comment.setDescription("Test Comment");

        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(proposal)).thenReturn(proposal);

        String userDocument = "123456";
        setAuthentication(userDocument, "password", "ROLE_USER");

        Proposal result = proposalService.comment(proposal.getId(), comment);

        assertNotNull(result);
        assertTrue(result.getComments().contains(comment));
        assertEquals(userDocument, comment.getUserDocument());
        assertEquals(LocalDate.now(), comment.getPublishDate());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void testCommentOnProposalNotFound() {
        String proposalId = "1";
        Comment comment = new Comment();
        comment.setDescription("Test Comment");

        when(proposalRepository.findById(proposalId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> proposalService.comment(proposalId, comment));
        assertEquals("Proposal not found", exception.getMessage());
    }

    @Test
    void testVoteOnProposal() {
        Proposal proposal = buildGenericProposal();
        proposal.setVotes(new ArrayList<>());

        String userDocument = "123456";

        Vote vote = new Vote();
        vote.setUserDocument(userDocument);
        vote.setInFavor(true);

        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(proposal)).thenReturn(proposal);

        setAuthentication(userDocument, "password", "ROLE_USER");

        Proposal result = proposalService.vote(proposal.getId(), vote);

        assertNotNull(result);
        assertTrue(result.getVotes().contains(vote));
        assertEquals(userDocument, vote.getUserDocument());
        verify(proposalRepository).save(proposal);
    }

    @Test
    void testVoteOnProposalNotFound() {
        String proposalId = "1";
        Vote vote = new Vote();
        vote.setUserDocument("123456");
        vote.setInFavor(true);

        when(proposalRepository.findById(proposalId)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> proposalService.vote(proposalId, vote));
        assertEquals("Proposal not found", exception.getMessage());
    }

    @Test
    void testVoteOnProposalDuplicateVote() {
        Proposal proposal = buildGenericProposal();

        Vote vote = new Vote();
        vote.setUserDocument("123456");
        vote.setInFavor(true);
        proposal.setVotes(new ArrayList<>());
        proposal.getVotes().add(vote);

        when(proposalRepository.findById(proposal.getId())).thenReturn(Optional.of(proposal));
        when(proposalRepository.save(proposal)).thenReturn(proposal);

        String userDocument = "123456";
        setAuthentication(userDocument, "password", "ROLE_USER");

        // El usuario vuelve a votar (esto debería eliminar su voto anterior y agregar el nuevo)
        vote.setInFavor(false);
        Proposal result = proposalService.vote(proposal.getId(), vote);

        assertNotNull(result);
        assertEquals(1, result.getVotes().size()); // Asegura que solo hay un voto del mismo usuario
        verify(proposalRepository).save(proposal);
    }

    private void setAuthentication(String userDocument, String password, String role) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDocument, password, authorities);

        // Se establece el Authentication en el SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private Proposal buildGenericProposal() {
        Proposal proposal = new Proposal();
        proposal.setId("1");
        proposal.setTitle("Crear ciclovía en el barrio Centro");
        proposal.setDescription("Crear una ciclovía en el barrio Centro para fomentar el uso de la bicicleta y reducir el tráfico.");
        proposal.setLimitDate(LocalDate.of(2025, 12, 31));
        proposal.setAuthorDocument("123456");
        return proposal;
    }

}
