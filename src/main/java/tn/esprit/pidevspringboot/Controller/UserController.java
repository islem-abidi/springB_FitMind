package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.JwtService;
import tn.esprit.pidevspringboot.dto.EmailRequest;
import tn.esprit.pidevspringboot.dto.LoginRequest;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.VerifyRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final Map<String, String> verificationCodes = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/")
    public String goHome() {
        return "This is publicly accessible without needing authentication.";
    }

    @Operation(summary = "Faire la registration", description = "Permet de faire le sign-up.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur enregistré avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    @PostMapping("/registration")
    public ResponseEntity<Object> saveUser(@RequestBody UserRequest userRequest) {
        System.out.println("Requête POST reçue sur /auth/registration : " + userRequest.getEmail());

        if (!userRequest.getEmail().matches("^[a-zA-Z]+\\.[a-zA-Z]+@esprit\\.tn$")) {
            return ResponseEntity.status(400).body("Invalid email format. Use nom.prenom@esprit.tn");
        }

        Optional<tn.esprit.pidevspringboot.Entities.User.User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("Email already in use.");
        }

        Role userRole = roleRepository.findById(userRequest.getId_role())
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        tn.esprit.pidevspringboot.Entities.User.User user = new tn.esprit.pidevspringboot.Entities.User.User();
        user.setNom(userRequest.getNom());
        user.setPrenom(userRequest.getPrenom());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setDateNaissance(userRequest.getDateNaissance());
        user.setSexe(userRequest.getSexe());
        user.setNumeroDeTelephone(userRequest.getNumeroDeTelephone());
        user.setPhotoProfil(userRequest.getPhotoProfil());
        user.setRole(userRole);

        userRepository.save(user);

        return ResponseEntity.ok("User was successfully registered with role ID " + userRole.getId_role());
    }

    @Operation(summary = "Envoyer le code de vérification", description = "Permet d'envoyer le code par email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    @PostMapping("/send-verification-code")
    public ResponseEntity<Object> sendVerificationCode(@RequestBody EmailRequest request) {
        String email = request.getEmail();

        if (!email.matches("^[a-zA-Z]+\\.[a-zA-Z]+@esprit\\.tn$")) {
            return ResponseEntity.status(400).body("Invalid email format. Use nom.prenom@esprit.tn");
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        try {
            sendEmail(email, verificationCode);
            verificationCodes.put(email, verificationCode);

            return ResponseEntity.ok("Verification code sent to " + email);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email.");
        }
    }

    @Operation(summary = "Authentification", description = "Permet de se connecter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentification réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<tn.esprit.pidevspringboot.Entities.User.User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(401).body("Invalid credentials: User not found");
            }

            tn.esprit.pidevspringboot.Entities.User.User user = optionalUser.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials: Incorrect password");
            }

            // ✅ Créer un UserDetails à partir de l'utilisateur pour le JWT
            UserDetails userDetails = User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().getRoleType().name()) // 🔹 Convertit l'enum en String
                    .build();

            // ✅ Générer le JWT après connexion réussie
            String token = jwtService.generateToken(userDetails);

            // ✅ Retourner le JWT dans la réponse
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful!");
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @Autowired
    public UserController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Your Verification Code");
        helper.setText("Your verification code is: " + code);
        mailSender.send(message);
    }
}
