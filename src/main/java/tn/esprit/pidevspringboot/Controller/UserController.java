package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.LoginEventRepository;
import tn.esprit.pidevspringboot.Repository.RoleRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.IUserService;
import tn.esprit.pidevspringboot.dto.*;
import org.springframework.data.domain.Pageable;


import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "BearerAuth")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private IUserService userService;
@Autowired private LoginEventRepository loginEventRepository;
    /// ///////////////CRUD : delete , update , get User
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    @GetMapping("/get/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @Operation(summary = "Archiver un utilisateur")
    @PutMapping("/archive/{idUser}")
    public void archiveUser(@PathVariable Long idUser) {
        userService.archiveUser(idUser);
    }
    @PutMapping("/restore/{id}")
    public ResponseEntity<Map<String, String>> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur restauré avec succès"));
    }


    @GetMapping("/filter")
    public ResponseEntity<List<UserResponse>> filterByField(
            @RequestParam String field,
            @RequestParam String value) {
        return ResponseEntity.ok(userService.filterByField(field, value));
    }
    @GetMapping("/users/sorted")
    public ResponseEntity<Page<UserResponse>> getUsersSortedByPrenom(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("prenom").ascending());
        return ResponseEntity.ok(userService.getUsersSortedByPrenom(pageable));
    }
    @GetMapping("/users/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }
 

    @GetMapping("/users/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.isEmailTaken(email);
        return ResponseEntity.ok(Collections.singletonMap("taken", exists));
    }
    @Autowired private PasswordEncoder passwordEncoder;

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserRequest update, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (update.getNom() != null) user.setNom(update.getNom());
        if (update.getPrenom() != null) user.setPrenom(update.getPrenom());
        if (update.getNumeroDeTelephone() != null) user.setNumeroDeTelephone(Math.toIntExact(update.getNumeroDeTelephone()));
        if(update.getSexe() != null) user.setSexe(update.getSexe());
        if(update.getDateNaissance() != null) user.setDateNaissance(update.getDateNaissance());
        if (update.getPhotoProfil() != null) {
            user.setPhotoProfil(update.getPhotoProfil());
        }

        userRepository.save(user);
        System.out.println("🛠 Reçu: " + update);

        return ResponseEntity.ok("✅ Profil mis à jour");
    }
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Token manquant ou invalide.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Construction manuelle du map en évitant les valeurs null
        Map<String, Object> response = new HashMap<>();
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());
        response.put("numeroDeTelephone", user.getNumeroDeTelephone());
        response.put("dateNaissance", user.getDateNaissance());
        response.put("sexe", user.getSexe() != null ? user.getSexe().name() : null);
        response.put("photoProfil", user.getPhotoProfil());

        return ResponseEntity.ok(response);
    }



    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("❌ Mot de passe actuel incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("❌ Les nouveaux mots de passe ne correspondent pas.");
        }

        if (!request.getNewPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")) {
            return ResponseEntity.badRequest().body("❌ Le nouveau mot de passe est trop faible.");
        }

        user.setMotDePasse(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("✅ Mot de passe mis à jour avec succès !");
    }


}