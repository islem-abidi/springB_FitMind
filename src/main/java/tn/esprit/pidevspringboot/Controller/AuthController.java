package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidevspringboot.Entities.User.LoginEvent;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.LoginEventRepository;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.CaptchaValidatorService;
import tn.esprit.pidevspringboot.Service.IUserService;
import tn.esprit.pidevspringboot.Service.IpLocationService;
import tn.esprit.pidevspringboot.Service.JwtService;
import tn.esprit.pidevspringboot.dto.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@RestController
@RequestMapping("/auth")
@SecurityRequirement(name = "BearerAuth")
public class AuthController {

    private final Map<String, CodeEntry> verificationCodes = new HashMap<>();

    private static class CodeEntry {
        String code;
        long timestamp;
        CodeEntry(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
@Autowired private CaptchaValidatorService captchaValidatorService;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JavaMailSender mailSender;
    @Autowired private JwtService jwtService;
    @Autowired private IUserService userService;
    @Autowired private IpLocationService ipLocationService;
    @Autowired private LoginEventRepository loginEventRepo;

    private UserRequest userRequest;
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> saveUser(
            @RequestPart("user") UserRequest userRequest,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        String nom = userRequest.getNom().toLowerCase();
        String prenom = userRequest.getPrenom().toLowerCase();
        String email = userRequest.getEmail().toLowerCase();
        String expected1 = prenom + "." + nom + "@esprit.tn";
        String expected2 = nom + "." + prenom + "@esprit.tn";
        String token = userRequest.getCaptchaToken(); // champ à ajouter dans SignupRequest DTO

        if (!captchaValidatorService.isCaptchaValid(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("reCAPTCHA validation échouée.");
        }


        if (!email.equals(expected1) && !email.equals(expected2)) {
            return ResponseEntity.badRequest().body("❌ Email invalide : " + expected1 + " ou " + expected2);
        }

        if (userService.isEmailTaken(email)) {
            return ResponseEntity.badRequest().body("❌ Email déjà utilisé.");
        }

        if (userRequest.getNumeroDeTelephone() == null ||
                !userRequest.getNumeroDeTelephone().toString().matches("^[2459]\\d{7}$")) {
            return ResponseEntity.badRequest().body("❌ Numéro invalide (format tunisien)");
        }

        if (userRequest.getDateNaissance() == null ||
                Period.between(
                        userRequest.getDateNaissance().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                        java.time.LocalDate.now()
                ).getYears() < 18) {
            return ResponseEntity.badRequest().body("❌ L'utilisateur doit avoir au moins 18 ans.");
        }

        String password = userRequest.getPassword();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")) {
            return ResponseEntity.badRequest().body("❌ Mot de passe invalide.");
        }

        Role userRole = roleRepository.findById(userRequest.getId_role())
                .orElseThrow(() -> new RuntimeException("❌ Rôle introuvable."));

        User user = new User();
        user.setNom(userRequest.getNom());
        user.setPrenom(userRequest.getPrenom());
        user.setEmail(email);
        user.setMotDePasse(passwordEncoder.encode(password));
        user.setDateNaissance(userRequest.getDateNaissance());
        user.setSexe(userRequest.getSexe());
        user.setNumeroDeTelephone(userRequest.getNumeroDeTelephone());

        if (photo != null && !photo.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(photo.getBytes());
            user.setPhotoProfil(base64Image);
        }
        System.out.println("📸 Fichier: " + (photo != null ? photo.getOriginalFilename() : "Aucun"));


        user.setRole(userRole);
        user.setIsVerified(false);
        userRepository.save(user);

        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, new CodeEntry(code, System.currentTimeMillis()));

        try {
            sendEmail(email, code, prenom, nom);
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("✔️ Inscription OK, mais erreur envoi code.");
        }

        return ResponseEntity.ok(Map.of(
                "message", "✔️ Inscription réussie. Code envoyé.",
                "email", email
        ));
    }



    @GetMapping("/check-login")
    public ResponseEntity<Boolean> isLoggedIn(Principal principal) {
        boolean isLogged = principal != null && principal.getName() != null;
        return ResponseEntity.ok(isLogged);
    }
    @PostMapping("/resend-code")
    public ResponseEntity<Object> resendCode(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        String code = verificationCodes.get(email).code;
        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("❌ Email non trouvé.");
        }

        String newCode = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, new CodeEntry(newCode, System.currentTimeMillis()));

        try {
            sendEmail(email, code, userRequest.getPrenom(), userRequest.getNom());

            return ResponseEntity.ok("📩 Nouveau code envoyé !");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("⚠️ Envoi du code échoué.");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Object> verifyCode(@RequestBody VerifyRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        CodeEntry entry = verificationCodes.get(email);
        if (entry == null) {
            return ResponseEntity.badRequest().body("❌ Aucune demande de vérification trouvée.");
        }

        if (System.currentTimeMillis() - entry.timestamp > 300_000) {
            return ResponseEntity.badRequest().body("❌ Code expiré. Demandez un nouveau.");
        }

        if (!entry.code.equals(code)) {
            return ResponseEntity.badRequest().body("❌ Code invalide.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsVerified(true);
            userRepository.save(user);
        }

        verificationCodes.remove(email);
        return ResponseEntity.ok("✅ Code vérifié avec succès ! Utilisateur activé.");
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("❌ Utilisateur introuvable.");
        }

        User user = optionalUser.get();

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body("❌ Veuillez vérifier votre email avant de vous connecter.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("❌ Mot de passe incorrect.");
        }
        LoginEvent event = LoginEvent.builder()
                .user(user)
                .loginDate(LocalDateTime.now())
                .build();
        loginEventRepo.save(event);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getRoleType().name())
                .build();

        String token = jwtService.generateToken(userDetails);
        String ip=request.getRemoteAddr();
        Map<String, Object> location = ipLocationService.getLocation(ip);


        return ResponseEntity.ok(Map.of(
                "message", "✅ Connexion réussie",
                "token", token,
                "role", user.getRole().getRoleType()
        ));

    }





    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("❌ Format de token invalide.");
        }

        jwtService.revokeToken(authHeader.substring(7));
        return ResponseEntity.ok("✅ Déconnexion réussie.");
    }

    private void sendEmail(String to, String code, String prenom, String nom) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String fullName = prenom + " " + nom;
        String verificationUrl = "http://localhost:4200/verify-code?email=" + to;

        String htmlContent = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Bonjour %s,</h2>
            <p>Merci pour votre inscription.</p>
            <p>Voici votre code de vérification :</p>
            <p style="font-size: 22px; font-weight: bold;">%s</p>
            <p>Ou cliquez ici pour vérifier votre compte :</p>
            <a href="%s"
               style="display: inline-block; padding: 10px 20px; background-color: #28a745; color: white;
                      text-decoration: none; border-radius: 5px;">
               Vérifier mon compte
            </a>
            <p style="margin-top: 20px;">À bientôt 👋</p>
        </body>
        </html>
    """, fullName, code, verificationUrl);

        helper.setTo(to);
        helper.setSubject("Votre code de vérification");
        helper.setText(htmlContent, true); // HTML enabled

        mailSender.send(message);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Email non trouvé.");
        }

        String token = UUID.randomUUID().toString();
        User user = optionalUser.get();
        user.setResetToken(token);
        user.setResetTokenTime(new Date());
        userRepository.save(user);
        resetTokens.put(token, user.getEmail());

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        try {
            sendResetPasswordEmail(user.getEmail(), resetLink, user.getPrenom());
            return ResponseEntity.ok("📩 Lien de réinitialisation envoyé.");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("⚠️ Erreur d'envoi de mail.");
        }
    }
    private boolean isStrongPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$");
    }
    private final Map<String, String> resetTokens = new HashMap<>();

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        if (!resetTokens.containsKey(token)) {
            return ResponseEntity.badRequest().body("❌ Code invalide ou expiré.");
        }

        String email = resetTokens.get(token);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Utilisateur introuvable.");
        }

        if (!isStrongPassword(newPassword)) {
            return ResponseEntity.badRequest().body("❌ Mot de passe faible.");
        }

        User user = optionalUser.get();
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokens.remove(token); // Invalider après usage

        // 🔐 Révoquer les anciens tokens
        jwtService.revokeAllTokensForUser(user);

        // 🎟 Générer un nouveau token
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getRoleType().name())
                .build();

        String newToken = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(Map.of(
                "message", "✔️ Mot de passe réinitialisé avec succès.",
                "token", newToken
        ));
    }

    private void sendResetPasswordEmail(String to, String link, String prenom) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("🔐 Réinitialisez votre mot de passe");
        helper.setText("<p>Bonjour " + prenom + ",</p>" +
                "<p>Voici le lien pour réinitialiser votre mot de passe :</p>" +
                "<p><a href=\"" + link + "\">Réinitialiser</a></p>", true);
        mailSender.send(message);
    }

}
