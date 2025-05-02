package platform.controller;

import entities.domain.Comment;
import entities.domain.Proposal;
import entities.domain.Vote;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import platform.service.ProposalService;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
public class ProposalController {

    private final ProposalService service;

    public ProposalController(ProposalService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('Mayor')")
    @GetMapping
    public List<Proposal> getAll() {
        return service.getAll();
    }

    @PreAuthorize("hasAuthority('Mayor')")
    @PostMapping
    public Proposal create(@RequestBody Proposal proposal) {
        return service.create(proposal);
    }

    @PreAuthorize("hasAuthority('Mayor')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @PreAuthorize("hasAuthority('Citizen')")
    @PostMapping("/{proposalId}/comment")
    public Proposal addComment(@PathVariable String proposalId, @RequestBody Comment comment) {
        return service.comment(proposalId, comment);
    }

    @PreAuthorize("hasAuthority('Citizen')")
    @PostMapping("/{proposalId}/vote")
    public Proposal vote(@PathVariable String proposalId, @RequestBody Vote vote) {
        return service.vote(proposalId, vote);
    }


}
