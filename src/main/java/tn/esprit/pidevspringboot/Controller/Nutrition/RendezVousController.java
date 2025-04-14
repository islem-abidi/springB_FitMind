package tn.esprit.pidevspringboot.Controller.Nutrition;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import tn.esprit.pidevspringboot.Service.Nutrition.IRendezVousServices;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rendezvous")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "RendezVous")
public class RendezVousController {

    private final IRendezVousServices rendezVousServices;
    private static final Logger logger = LoggerFactory.getLogger(RendezVousController.class);

    public RendezVousController(IRendezVousServices rendezVousServices) {
        this.rendezVousServices = rendezVousServices;
    }

    @GetMapping("/retrieveAllRendezVous")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        List<RendezVous> rendezVousList = rendezVousServices.retrieveAllRendezVous();
        return ResponseEntity.ok(rendezVousList);
    }

    @GetMapping("/retrieveRendezVous/{id}")
    public ResponseEntity<RendezVous> getRendezVous(@PathVariable Long id) {
        try {
            RendezVous rendezVous = rendezVousServices.retrieveRendezVous(id);
            return ResponseEntity.ok(rendezVous);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du rendez-vous avec ID: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/addRendezVous")
    public ResponseEntity<?> addRendezVous(@Valid @RequestBody RendezVous rendezVous) {
        try {
            RendezVous saved = rendezVousServices.addRendezVous(rendezVous);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la création du rendez-vous", e);
            return ResponseEntity.internalServerError().body("Erreur serveur.");
        }
    }

    @PutMapping("/updateRendezVous/{id}")
    public ResponseEntity<?> updateRendezVous(@PathVariable Long id, @Valid @RequestBody RendezVous rendezVous) {
        try {
            rendezVous.setIdRendezVous(id);
            RendezVous updatedRendezVous = rendezVousServices.updateRendezVous(rendezVous);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du rendez-vous", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mise à jour échouée: " + e.getMessage());
        }
    }

    @PutMapping("/archiveRendezVous/{id}")
    public ResponseEntity<?> archiveRendezVous(@PathVariable Long id) {
        try {
            RendezVous updatedRendezVous = rendezVousServices.archiveRendezVous(id);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (Exception e) {
            logger.error("Erreur lors de l'archivage du rendez-vous", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'archivage du rendez-vous : " + e.getMessage());
        }
    }

    @PutMapping("/updateStatutRendezVous/{id}")
    public ResponseEntity<?> updateStatutRendezVous(@PathVariable Long id, @RequestBody Map<String, String> updatedStatus) {
        try {
            // Vérifier si le statut est présent dans la requête
            String statut = updatedStatus.get("statut");
            if (statut == null || statut.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Le statut est requis.");
            }

            // Mise à jour du statut dans le service
            rendezVousServices.updateStatutRendezVous(id, statut);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Si le statut est invalide, retourner une erreur spécifique
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du statut du rendez-vous avec ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur serveur lors de la mise à jour du statut.");
        }
    }


}
