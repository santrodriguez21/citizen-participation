package platform.controller;

import entities.login.LoginRequest;
import entities.user.Citizen;
import entities.user.Mayor;
import entities.user.Moderator;
import entities.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import platform.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('Moderator')")
    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping("/citizen")
    public Citizen createCitizen(@RequestBody Citizen citizen) {
        return userService.createCitizen(citizen);
    }

    @PostMapping("/mayor")
    public Mayor createMayor(@RequestBody Mayor mayor) {
        return userService.createMayor(mayor);
    }

    @PostMapping("/moderator")
    public Moderator createModerator(@RequestBody Moderator moderator) {
        return userService.createModerator(moderator);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
       return userService.login(request.getEmail(), request.getPassword());
    }

    @PreAuthorize("hasAuthority('Moderator')")
    @DeleteMapping("/{document}")
    public void delete(@PathVariable String document) {
        userService.delete(document);
    }

    @PutMapping
    public void modify(@RequestBody User user) {
        userService.modify(user);
    }
}
