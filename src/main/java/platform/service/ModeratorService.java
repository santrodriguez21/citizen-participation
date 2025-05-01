package platform.service;

import domain.Proposal;
import org.springframework.stereotype.Service;
import platform.repository.ProposalRepository;

@Service
public class ModeratorService {

    private final ProposalRepository repository;

    public ModeratorService(ProposalRepository repository) {
        this.repository = repository;
    }

    public void deleteComment(String proposalId, String commentId) {
        Proposal proposal = repository.findById(proposalId).orElseThrow(() -> new RuntimeException("Proposal not found"));
        proposal.getComments().removeIf(comment -> comment.getId().equals(commentId));
    }
}
