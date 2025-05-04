package platform.service;

import entities.domain.Proposal;
import exception.BadRequestException;
import org.springframework.stereotype.Service;
import platform.repository.ProposalRepository;

@Service
public class ModeratorService {

    private final ProposalRepository repository;

    public ModeratorService(ProposalRepository repository) {
        this.repository = repository;
    }

    public void deleteComment(String proposalId, String commentId) {
        Proposal proposal = repository.findById(proposalId)
                .orElseThrow(() -> new BadRequestException("Proposal not found"));

        boolean removed = proposal.getComments().removeIf(comment -> comment.getId().equals(commentId));

        if (!removed) {
            throw new BadRequestException("Comment not found");
        }

        repository.save(proposal);
    }
}
