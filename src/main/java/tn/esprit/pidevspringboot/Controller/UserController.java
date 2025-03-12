package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Config.JwtUtil;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tn.esprit.pidevspringboot.dto.EmailRequest;
import tn.esprit.pidevspringboot.dto.LoginRequest;
import tn.esprit.pidevspringboot.dto.UserRequest;
import tn.esprit.pidevspringboot.dto.VerifyRequest;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final Map<String, String> verificationCodes = new HashMap<>(); // Stocke les codes envoyés

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/")
    public String goHome(){
        return "This is publicly accessible without needing authentication.";
    }
    @Operation(summary = "faire la registration", description = "Permet de faire le sign up .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user enregistré avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    //  ENREGISTRER UN UTILISATEUR (SIGN-UP)
    @PostMapping("/registration")
    public ResponseEntity<Object> saveUser(@RequestBody UserRequest userRequest) {
        System.out.println("Requête POST reçue sur /auth/registration : " + userRequest.getEmail());

        // Vérifier si l'email suit le format nom.prenom@esprit.tn
        if (!userRequest.getEmail().matches("^[a-zA-Z]+\\.[a-zA-Z]+@esprit\\.tn$")) {
            return ResponseEntity.status(400).body("Invalid email format. Use nom.prenom@esprit.tn");
        }

        Optional<User> existingUser = userRepository.findByEmail(userRequest.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(400).body("Email already in use.");
        }

        Role userRole = roleRepository.findById(userRequest.getId_role())
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        // Créer l'utilisateur avec id_role
        User user = new User();
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
    @Operation(summary = "envoyer le code verification", description = "Permet d'envoyer le code par mail .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "code envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    // 🔹 ENVOYER LE CODE OTP PAR EMAIL

    @PostMapping("/send-verification-code")
    public ResponseEntity<Object> sendVerificationCode(@RequestBody EmailRequest request) {
        String email = request.getEmail();

        if (!email.matches("^[a-zA-Z]+\\.[a-zA-Z]+@esprit\\.tn$")) {
            return ResponseEntity.status(400).body("Invalid email format. Use nom.prenom@esprit.tn");
        }

        // Génération du code OTP
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        try {
            sendEmail(email, verificationCode);
            verificationCodes.put(email, verificationCode);


            System.out.println("Code OTP stocké : " + email + " -> " + verificationCode);
            System.out.println("Verification Codes Map: " + verificationCodes);

            return ResponseEntity.ok("Verification code sent to " + email);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email.");
        }
    }
    @Operation(summary = "faire le sign in", description = "Permet s'authentifier .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "authentification avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    // ✅ LOGIN (Génère et stocke un JWT)
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = optionalUser.get();

        // 🔥 Vérifier si le mot de passe correspond après encodage
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok("Login Successful!");
    }

    // ✅ Récupérer un Token stocké
    @GetMapping("/token/{email}")
    public ResponseEntity<Object> getToken(@PathVariable String email) {
        String token = jwtUtil.getToken(email);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(" Token non trouvé");
        }
        return ResponseEntity.ok(Map.of("token", token));
    }



    @Autowired
    public UserController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // 🔹 MÉTHODE POUR ENVOYER UN EMAIL AVEC LE CODE OTP
    private void sendEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Your Verification Code");
        helper.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    // 🔹 VÉRIFIER LE CODE OTP
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@RequestBody VerifyRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        System.out.println("Verification Codes before checking: " + verificationCodes);
        System.out.println("Email reçu: " + email + " - Code reçu: " + code);

        if (!verificationCodes.containsKey(email)) {
            return ResponseEntity.status(400).body("No verification code found for this email.");
        }

        if (!verificationCodes.get(email).equals(code)) {
            return ResponseEntity.status(400).body("Invalid verification code.");
        }

        verificationCodes.remove(email);

        // ✅ Générer un token JWT après vérification réussie
        String token = jwtUtil.generateToken(email);
        System.out.println("Token JWT généré : " + token);

        // ✅ Retourner le token JWT dans la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User authenticated successfully!");
        response.put("token", token);

        return ResponseEntity.ok(response);
    }




}
