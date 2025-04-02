package tn.esprit.pidevspringboot.Controller.Nutrition;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import tn.esprit.pidevspringboot.Service.Nutrition.IRendezVousServices;
import java.util.List;

@RestController
@RequestMapping("/rendezvous")
@Tag(name = "RendezVous")
public class RendezVousController {

    private final IRendezVousServices rendezVousServices;

    // Injection de dépendance via le constructeur
    public RendezVousController(IRendezVousServices rendezVousServices) {
        this.rendezVousServices = rendezVousServices;
    }

    @GetMapping("/retrieveAllRendezVous")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousServices.retrieveAllRendezVous());
    }

    @GetMapping("/retrieveRendezVous/{id}")
    public ResponseEntity<RendezVous> getRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousServices.retrieveRendezVous(id));
    }
    private static final Logger logger = LoggerFactory.getLogger(RendezVousController.class);

    @PostMapping("/addRendezVous")
    public ResponseEntity<?> addRendezVous(@RequestBody RendezVous rendezVous) {
        try {
            RendezVous createdRendezVous = rendezVousServices.addRendezVous(rendezVous);
            return ResponseEntity.ok(createdRendezVous);
        } catch (Exception e) {
            logger.error("Erreur lors de la création du rendez-vous", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du rendez-vous : " + e.getMessage());
        }
    }

    @PutMapping("/updateRendezVous/{id}")
    public ResponseEntity<RendezVous> updateRendezVous(@PathVariable Long id, @RequestBody RendezVous rendezVous) {
        rendezVous.setIdRendezVous(id);
        return ResponseEntity.ok(rendezVousServices.updateRendezVous(rendezVous));
    }

    @PutMapping("/archiveRendezVous/{id}")
    public ResponseEntity<RendezVous> archiveRendezVous(@PathVariable Long id) {
        try {
            RendezVous updatedRendezVous = rendezVousServices.archiveRendezVous(id);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
