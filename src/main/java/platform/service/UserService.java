package platform.service;

import entities.user.Citizen;
import entities.user.Mayor;
import entities.user.Moderator;
import entities.user.User;
import exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import platform.repository.UserRepository;
import security.JwtResponse;
import security.JwtService;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public String createCitizen(Citizen citizen) {
        validateDataAndEncodePassword(citizen);
        userRepository.save(citizen);
        return "Citizen created successfully";
    }

    public String createModerator(Moderator moderator) {
        validateDataAndEncodePassword(moderator);
        userRepository.save(moderator);
        return "Moderator created successfully";
    }

    public String createMayor(Mayor mayor) {
        validateDataAndEncodePassword(mayor);
        userRepository.save(mayor);
        return "Mayor created successfully";
    }

    public ResponseEntity<?> login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new BadRequestException("User not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
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

    private void validateDataAndEncodePassword(User user) {
        validateUserData(user);
        encodePassword(user);
    }

    private void validateUserData(User user) {
        if(user.getName() == null || user.getDocument() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new BadRequestException("All fields are required");
        }
        if(userExists(user.getDocument())){
            throw new BadRequestException("There is already a user with that document");
        }
        if(!isValidEmail(user.getEmail())){
            throw new BadRequestException("Invalid email format");
        }
        if(userExistsByEmail(user.getEmail())){
            throw new BadRequestException("There is already a user with that email");
        }
        if(!isValidUruguayanDocument(user.getDocument())){
            throw new BadRequestException("Invalid document format");
        }
    }

    private boolean userExists(String document) {
        return userRepository.findByDocument(document) != null;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    private boolean isValidUruguayanDocument(String ci) {
        if (ci == null || !ci.matches("\\d{7,8}")) return false;

        while (ci.length() < 8) {
            ci = "0" + ci;
        }

        int[] weights = {2, 9, 8, 7, 6, 3, 4};
        int sum = 0;

        for (int i = 0; i < 7; i++) {
            int digit = Character.getNumericValue(ci.charAt(i));
            sum += digit * weights[i];
        }

        int expectedCheckDigit = (10 - (sum % 10)) % 10;
        int actualCheckDigit = Character.getNumericValue(ci.charAt(7));

        return expectedCheckDigit == actualCheckDigit;
    }

    private void encodePassword(User user) {
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
    }

}
