package platform.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.service.ModeratorService;

@RestController
@RequestMapping("/api/moderator")
public class ModeratorController {

    private final ModeratorService service;

    public ModeratorController(ModeratorService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('Moderator')")
    @PostMapping("/{proposalId}/deleteComment")
    public void deleteComment(@PathVariable String proposalId, @RequestBody String commentId) {
        service.deleteComment(proposalId, commentId);
    }

}
