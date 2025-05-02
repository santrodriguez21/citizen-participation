package platform.service;

import entities.domain.Comment;
import entities.domain.Proposal;
import entities.domain.Vote;
import exception.BadRequestException;
import org.springframework.stereotype.Service;
import platform.repository.ProposalRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProposalService {

    private final ProposalRepository repository;

    public ProposalService(ProposalRepository repository) {
        this.repository = repository;
    }

    public List<Proposal> getAll() {
        return repository.findAll();
    }

    public Proposal create(Proposal proposal) {
        String userDocument = SecurityContextHolder.getContext().getAuthentication().getName();
        proposal.setAuthorDocument(userDocument);
        return repository.save(proposal);
    }

    public void delete(String id) {
        Proposal proposal = repository.findById(id)
                .orElseThrow(() -> new BadRequestException("Proposal not found"));

        String userDocument = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!proposal.getAuthorDocument().equals(userDocument)){
            throw new BadRequestException("Only the author can delete the proposal");
        }
        repository.deleteById(id);
    }

    public Proposal comment(String proposalId, Comment comment) {
        Proposal proposal = repository.findById(proposalId)
                .orElseThrow(() -> new BadRequestException("Proposal not found"));

        String userDocument = SecurityContextHolder.getContext().getAuthentication().getName();

        comment.setUserDocument(userDocument);
        comment.setPublishDate(LocalDate.now());

        if (proposal.getComments() == null) proposal.setComments(new ArrayList<>());
        proposal.getComments().add(comment);

        return repository.save(proposal);
    }

    public Proposal vote(String proposalId, Vote vote) {
        Proposal proposal = repository.findById(proposalId)
                .orElseThrow(() -> new BadRequestException("Proposal not found"));

        String userDocument = SecurityContextHolder.getContext().getAuthentication().getName();

        if (proposal.getVotes() == null) proposal.setVotes(new ArrayList<>());
        vote.setUserDocument(userDocument);

        if(proposal.getVotes().stream().anyMatch(v -> v.getUserDocument().equals(userDocument))){
            proposal.getVotes().removeIf(v -> v.getUserDocument().equals(userDocument));
        }
        proposal.getVotes().add(vote);

        return repository.save(proposal);
    }
}
