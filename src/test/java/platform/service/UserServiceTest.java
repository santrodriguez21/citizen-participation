package platform.service;

import entities.user.Citizen;
import entities.user.Mayor;
import entities.user.Moderator;
import entities.user.User;

import exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import platform.repository.UserRepository;
import security.JwtResponse;
import security.JwtService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    private Citizen sampleCitizen;
    private Moderator sampleModerator;
    private Mayor sampleMayor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sampleCitizen = new Citizen();
        sampleCitizen.setName("Citizen Name");
        sampleCitizen.setDocument("49359161");
        sampleCitizen.setEmail("citizen@example.com");
        sampleCitizen.setPassword("1234");
        sampleCitizen.setAddress("Citizen Address");

        sampleModerator = new Moderator();
        sampleModerator.setName("Moderator");
        sampleModerator.setDocument("64246717");
        sampleModerator.setEmail("mod@example.com");
        sampleModerator.setPassword("1234");
        sampleModerator.setAddress("Moderator Address");

        sampleMayor = new Mayor();
        sampleMayor.setName("Mayor");
        sampleMayor.setDocument("41162211");
        sampleMayor.setEmail("mayor@example.com");
        sampleMayor.setPassword("1234");
        sampleMayor.setAddress("Mayor Address");
    }

    @Test
    void testGetAll() {
        when(userRepository.findAll()).thenReturn(List.of(sampleCitizen));
        List<User> users = userService.getAll();
        assertEquals(1, users.size());
        assertEquals("Citizen Name", users.getFirst().getName());
    }

    @Test
    void testCreateCitizen_success() {
        when(userRepository.findByDocument("49359161")).thenReturn(null);
        when(userRepository.findByEmail("citizen@example.com")).thenReturn(null);
        when(passwordEncoder.encode("1234")).thenReturn("encodedPwd");

        String result = userService.createCitizen(sampleCitizen);

        assertEquals("Citizen created successfully", result);
        verify(userRepository).save(any(Citizen.class));
    }

    @Test
    void testCreateModerator_success() {
        when(userRepository.findByDocument("64246717")).thenReturn(null);
        when(userRepository.findByEmail("mod@example.com")).thenReturn(null);
        when(passwordEncoder.encode("1234")).thenReturn("encodedPwd");

        String result = userService.createModerator(sampleModerator);

        assertEquals("Moderator created successfully", result);
        verify(userRepository).save(any(Moderator.class));
    }

    @Test
    void testCreateMayor_success() {
        when(userRepository.findByDocument("41162211")).thenReturn(null);
        when(userRepository.findByEmail("mayor@example.com")).thenReturn(null);
        when(passwordEncoder.encode("1234")).thenReturn("encodedPwd");

        String result = userService.createMayor(sampleMayor);

        assertEquals("Mayor created successfully", result);
        verify(userRepository).save(any(Mayor.class));
    }

    @Test
    void testLogin_success() {
        sampleCitizen.setPassword("encodedPwd");

        when(userRepository.findByEmail("citizen@example.com")).thenReturn(sampleCitizen);
        when(passwordEncoder.matches("1234", "encodedPwd")).thenReturn(true);
        when(jwtService.generateToken("49359161", "Citizen")).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = userService.login("citizen@example.com", "1234");

        assertEquals("fake-jwt-token", ((JwtResponse) Objects.requireNonNull(response.getBody())).getToken());
    }

    @Test
    void testLogin_invalidPassword() {
        sampleCitizen.setPassword("encodedPwd");

        when(userRepository.findByEmail("citizen@example.com")).thenReturn(sampleCitizen);
        when(passwordEncoder.matches("wrong", "encodedPwd")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> userService.login("citizen@example.com", "wrong"));
    }

    @Test
    void testLogin_userNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> userService.login("notfound@example.com", "1234"));
    }

    @Test
    void testDelete_callsRepository() {
        userService.delete("49359161");
        verify(userRepository).deleteByDocument("49359161");
    }

    @Test
    void testModify_success() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("49359161");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User existing = new Citizen();
        existing.setName("Old Name");
        existing.setDocument("49359161");
        existing.setEmail("old@example.com");
        existing.setPassword("oldpwd");

        User modified = new Citizen();
        modified.setName("New Name");

        when(userRepository.findByDocument("49359161")).thenReturn(existing);

        userService.modify(modified);

        assertEquals("New Name", existing.getName());
        verify(userRepository).save(existing);
    }

    @Test
    void testModify_userNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("49359161");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByDocument("49359161")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> userService.modify(new Citizen()));
    }

    @Test
    void testEmailValidation() {
        Citizen invalidCitizen = new Citizen();
        invalidCitizen.setName("Invalid Email User");
        invalidCitizen.setDocument("49359161");
        invalidCitizen.setEmail("invalid-email");  // Email con formato incorrecto
        invalidCitizen.setPassword("1234");
        invalidCitizen.setAddress("Some Address");

        assertThrows(BadRequestException.class, () -> userService.createCitizen(invalidCitizen));
    }


    @Test
    void testDocumentValidation() {
        Citizen citizenWrongUruguayanDocument = new Citizen();
        citizenWrongUruguayanDocument.setName("Santi");
        citizenWrongUruguayanDocument.setDocument("49359162");
        citizenWrongUruguayanDocument.setEmail("valid@example.com");
        citizenWrongUruguayanDocument.setPassword("validPassword123");
        citizenWrongUruguayanDocument.setAddress("Some Address");
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWrongUruguayanDocument));
    }

    @Test
    void testEmptyFieldsValidation() {
        // Validar nombre vacío
        Citizen citizenWithEmptyName = new Citizen();
        citizenWithEmptyName.setName("");  // Campo nombre vacío
        citizenWithEmptyName.setDocument("12345678");
        citizenWithEmptyName.setEmail("valid@example.com");
        citizenWithEmptyName.setPassword("validPassword123");
        citizenWithEmptyName.setAddress("Some Address");
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWithEmptyName));

        // Validar documento vacío
        Citizen citizenWithEmptyDocument = new Citizen();
        citizenWithEmptyDocument.setName("Valid Name");
        citizenWithEmptyDocument.setDocument("");  // Campo documento vacío
        citizenWithEmptyDocument.setEmail("valid@example.com");
        citizenWithEmptyDocument.setPassword("validPassword123");
        citizenWithEmptyDocument.setAddress("Some Address");
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWithEmptyDocument));

        // Validar email vacío
        Citizen citizenWithEmptyEmail = new Citizen();
        citizenWithEmptyEmail.setName("Valid Name");
        citizenWithEmptyEmail.setDocument("12345678");
        citizenWithEmptyEmail.setEmail("");  // Campo email vacío
        citizenWithEmptyEmail.setPassword("validPassword123");
        citizenWithEmptyEmail.setAddress("Some Address");
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWithEmptyEmail));

        // Validar contraseña vacía
        Citizen citizenWithEmptyPassword = new Citizen();
        citizenWithEmptyPassword.setName("Valid Name");
        citizenWithEmptyPassword.setDocument("12345678");
        citizenWithEmptyPassword.setEmail("valid@example.com");
        citizenWithEmptyPassword.setPassword("");  // Campo contraseña vacío
        citizenWithEmptyPassword.setAddress("Some Address");
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWithEmptyPassword));

        // Validar dirección vacía
        Citizen citizenWithEmptyAddress = new Citizen();
        citizenWithEmptyAddress.setName("Valid Name");
        citizenWithEmptyAddress.setDocument("12345678");
        citizenWithEmptyAddress.setEmail("valid@example.com");
        citizenWithEmptyAddress.setPassword("validPassword123");
        citizenWithEmptyAddress.setAddress("");  // Campo dirección vacío
        assertThrows(BadRequestException.class, () -> userService.createCitizen(citizenWithEmptyAddress));
    }


    @Test
    void testCreateUserAlreadyExists() {
        Citizen existingCitizen = new Citizen();
        existingCitizen.setName("Existing User");
        existingCitizen.setDocument("49359161");  // Este documento ya está en la base de datos
        existingCitizen.setEmail("existing@example.com");
        existingCitizen.setPassword("1234");
        existingCitizen.setAddress("Existing Address");

        // Simular que el repositorio ya tiene un usuario con ese documento
        when(userRepository.findByDocument("49359161")).thenReturn(existingCitizen);

        // Creo un nuevo usuario con el mismo documento
        Citizen newCitizen = new Citizen();
        newCitizen.setName("New User");
        newCitizen.setDocument("49359161");
        newCitizen.setEmail("new@example.com");
        newCitizen.setPassword("1234");
        newCitizen.setAddress("New Address");

        assertThrows(BadRequestException.class, () -> userService.createCitizen(newCitizen));
    }
}
