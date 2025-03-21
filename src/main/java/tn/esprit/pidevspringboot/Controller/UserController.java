package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import tn.esprit.pidevspringboot.Entities.User.Token;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.TokenRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.JwtService;
import tn.esprit.pidevspringboot.dto.EmailRequest;
import tn.esprit.pidevspringboot.dto.LoginRequest;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.VerifyRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "BearerAuth")
public class UserController {
    private final Map<String, String> verificationCodes = new HashMap<>();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

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

        // 🔹 Générer et envoyer le code de vérification automatiquement
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        try {
            sendEmail(user.getEmail(), verificationCode);
            verificationCodes.put(user.getEmail(), verificationCode);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("User registered, but failed to send verification code.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully. Verification code sent.");
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@RequestBody VerifyRequest request){
        return ResponseEntity.status(200).body(verificationCodes.get(request.getCode()));
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

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().getRoleType().name()) // 🔹 Stocke le rôle
                    .build();

            // ✅ Générer un nouveau token et supprimer l’ancien
            String token = jwtService.generateToken(userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful!");
            response.put("token", token);
            response.put("role", user.getRole().getRoleType()); // 🔥 Associe le rôle

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtenir les informations de l'utilisateur connecté", description = "Nécessite un JWT valide.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur retournées"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<Object> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // Supprime "Bearer " pour garder le token brut
            }

            String email = jwtService.extractUsername(token);
            Optional<tn.esprit.pidevspringboot.Entities.User.User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Utilisateur non trouvé");
            }

            tn.esprit.pidevspringboot.Entities.User.User user = userOptional.get();
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", user.getEmail());
            userInfo.put("nom", user.getNom());
            userInfo.put("prenom", user.getPrenom());
            userInfo.put("role", user.getRole().getRoleType());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token invalide ou expiré");
        }
    }
    @Operation(summary = "Déconnexion", description = "Permet de se déconnecter et d'invalider le token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "400", description = "Token non valide")
    })
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("Invalid token format");
        }

        String token = authHeader.substring(7);
        jwtService.revokeToken(token);

        return ResponseEntity.ok("User successfully logged out and token invalidated.");
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
