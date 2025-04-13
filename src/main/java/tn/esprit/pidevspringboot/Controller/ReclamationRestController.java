package tn.esprit.pidevspringboot.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.BadWordFilterService;
import tn.esprit.pidevspringboot.Service.IReclamationService;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;
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
    public ResponseEntity<?> addReclamationByUser(@RequestBody ReclamationRequest dto, Principal principal) {
        if (badWordFilterService.containsBadWords(dto.getDescription())) {
            return ResponseEntity.badRequest().body("❌ Langage inapproprié détecté.");
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(user);
        reclamation.setDateReclamation(new Date());
        reclamation.setTypeReclamation(dto.getTypeReclamation());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut(Reclamation.StatutReclamation.En_Cours);
        reclamation.setArchived(false);

        return ResponseEntity.ok(reclamationRepository.save(reclamation));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReclamationByAdmin(@PathVariable Integer id, @RequestBody ReclamationRequest dto) {
        Reclamation reclamation = reclamationService.getReclamationById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));

        if (dto.getStatut() != null) {
            reclamation.setStatut(dto.getStatut());
        }
        if (dto.getDateResolution() != null) {
            reclamation.setDateResolution(dto.getDateResolution());
        }

        return ResponseEntity.ok(reclamationRepository.save(reclamation));
    }

    @GetMapping("/getall")
    public ResponseEntity<List<ReclamationResponseDTO>> getAllReclamations() {
        return ResponseEntity.ok(reclamationService.getAllReclamationsDTO());
    }


    @GetMapping("/get/{id}")
    public Optional<Reclamation> getReclamationById(@PathVariable Integer id) {
        return reclamationService.getReclamationById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteReclamation(@PathVariable Integer id) {
        reclamationService.deleteReclamation(id);
    }

    @GetMapping("/archived")
    public List<Reclamation> getArchivedReclamations() {
        return reclamationService.getArchivedReclamations();
    }

    @PutMapping("/restore/{id}")
    public Reclamation restoreReclamation(@PathVariable Integer id) {
        return reclamationService.restoreReclamation(id);
    }
}
