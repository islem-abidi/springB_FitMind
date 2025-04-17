package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import tn.esprit.pidevspringboot.Entities.User.Roletype;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.BadWordFilterService;
import tn.esprit.pidevspringboot.Service.IReclamationService;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;
import tn.esprit.pidevspringboot.dto.ReclamationResponse;
import tn.esprit.pidevspringboot.dto.ReclamationResponseDTO;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reclamations")
public class ReclamationRestController {

    @Autowired
    private IReclamationService reclamationService;

    @Autowired
    private BadWordFilterService badWordFilterService;

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Ajouter une réclamation", description = "Permet d'ajouter une nouvelle réclamation par l'utilisateur connecté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })


    @PostMapping("/save")
    @PreAuthorize("hasRole('Etudiant')")
    public ResponseEntity<ReclamationResponse> addReclamationByUser(@RequestBody ReclamationRequest dto, Principal principal) {
        User etudiant = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Reclamation saved = reclamationService.createReclamation(etudiant, dto);
        ReclamationResponse response = new ReclamationResponse(
                saved.getIdReclamation(),
                saved.getTypeReclamation().toString(),
                saved.getDescription(),
                saved.getStatut().toString(),
                saved.getDateReclamation(),
                saved.getDateResolution()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> updateReclamationByAdmin(@PathVariable Integer id, @RequestBody ReclamationRequest dto, Principal principal) {
        User admin = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Administrateur introuvable"));

        Reclamation updated = reclamationService.updateReclamationByAdmin(id, dto, admin);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/archive/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> archiveReclamation(@PathVariable Integer id) {
        reclamationService.deleteReclamation(id);
        return ResponseEntity.ok("Réclamation archivée avec succès.");
    }


    @GetMapping("/getall")
    public ResponseEntity<List<ReclamationResponseDTO>> getAllReclamations() {
        return ResponseEntity.ok(reclamationService.getAllReclamationsDTO(true)); // ou false si tu veux filtrer
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReclamationById(@PathVariable Integer id, Principal principal) {
        Reclamation r = reclamationService.getReclamationById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation introuvable"));

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!user.getRole().getRoleType().equals(Roletype.Admin)) {
            return ResponseEntity.status(403).body("Accès interdit.");
        }

        return ResponseEntity.ok(r);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReclamation(@PathVariable Integer id, Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!user.getRole().getRoleType().equals(Roletype.Admin)) {
            return ResponseEntity.status(403).body("Seuls les administrateurs peuvent supprimer une réclamation.");
        }

        reclamationService.deleteReclamation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/archived")
    public List<Reclamation> getArchivedReclamations() {
        return reclamationService.getArchivedReclamations();
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<?> restoreReclamation(@PathVariable Integer id) {
        Reclamation r = reclamationService.restoreReclamation(id);
        return ResponseEntity.ok("Réclamation restaurée avec succès.");
    }

    @GetMapping("/mes-reclamations")
    public ResponseEntity<List<ReclamationResponse>> getMesReclamations(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<ReclamationResponse> list = reclamationService.getMesReclamationsResponse(user);
        return ResponseEntity.ok(list);
    }

}
