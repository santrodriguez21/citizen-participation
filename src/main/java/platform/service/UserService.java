package platform.service;

import entities.user.Citizen;
import entities.user.Mayor;
import entities.user.Moderator;
import entities.user.User;
import exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import platform.repository.UserRepository;
import security.JwtResponse;
import security.JwtService;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Citizen createCitizen(Citizen citizen) {
        if (userExists(citizen.getDocument())) {
            throw new BadRequestException("There is already a user with that document");
        }
        return userRepository.save(citizen);
    }

    public Moderator createModerator(Moderator moderator) {
        if (userExists(moderator.getDocument())) {
            throw new BadRequestException("There is already a user with that document");
        }
        return userRepository.save(moderator);
    }

    public Mayor createMayor(Mayor mayor) {
        if (userExists(mayor.getDocument())) {
            throw new BadRequestException("There is already a user with that document");
        }
        return userRepository.save(mayor);
    }

    public ResponseEntity<?> login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new BadRequestException("User not found");
        }

        if (!password.equals(user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String role = switch (user) {
            case Moderator moderator -> "Moderator";
            case Citizen citizen -> "Citizen";
            case Mayor mayor -> "Mayor";
            default -> throw new BadRequestException("Unknown role");
        };

        String token = jwtService.generateToken(user.getDocument(), role);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public void delete(String document) {
        userRepository.deleteByDocument(document);
    }

    public void modify(User user) {
        String userDocument = SecurityContextHolder.getContext().getAuthentication().getName();

        User existingUser = userRepository.findByDocument(userDocument);
        if(existingUser==null){
            throw new BadRequestException("User not found");
        }

        existingUser.setName(user.getName() != null ? user.getName() : existingUser.getName());
        existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
        existingUser.setPassword(user.getPassword() !=null ? user.getPassword() : existingUser.getPassword());
        userRepository.save(existingUser);
    }

    private boolean userExists(String document) {
        return userRepository.findByDocument(document) != null;
    }

}
