package tn.esprit.pidevspringboot.Controller.Nutrition;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import tn.esprit.pidevspringboot.Service.Nutrition.IDossierMedicalServices;

import java.util.List;

@RestController
@Tag(name = "DossierMedical")
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/dossierMedical")
public class DossierMedicalController {


    private final IDossierMedicalServices idossierMedicalServices;

    public DossierMedicalController(IDossierMedicalServices idossierMedicalServices) {
        this.idossierMedicalServices = idossierMedicalServices;
    }

    @GetMapping("/retrieveAllDossiers")
    public ResponseEntity<List<DossierMedical>> getAllDossiers() {
        List<DossierMedical> dossiers = idossierMedicalServices.retrieveAllDossiers();
        return ResponseEntity.ok(dossiers);
    }

    @GetMapping("/retrieveDossier/{id}")
    public ResponseEntity<?> getDossierById(@PathVariable Long id) {
        try {
            DossierMedical dossier = idossierMedicalServices.retrieveDossier(id);
            return ResponseEntity.ok(dossier);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dossier non trouvé : " + e.getMessage());
        }
    }

    @PostMapping("/addDossier")
    public ResponseEntity<?> addDossier(@Valid @RequestBody DossierMedical dossierMedical) {
        try {
            DossierMedical addedDossier = idossierMedicalServices.addDossier(dossierMedical);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedDossier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne du serveur : " + e.getMessage());
        }
    }

    @PutMapping("/updateDossier")
    public ResponseEntity<?> updateDossier(@Valid @RequestBody DossierMedical dossierMedical) {
        try {
            DossierMedical updatedDossier = idossierMedicalServices.updateDossier(dossierMedical);
            return ResponseEntity.ok(updatedDossier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    @PutMapping("/archiveDossier/{id}")
    public ResponseEntity<?> archiveDossier(@PathVariable Long id) {
        try {
            DossierMedical archivedDossier = idossierMedicalServices.archiveDossier(id);
            return ResponseEntity.ok(archivedDossier);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dossier non trouvé : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'archivage : " + e.getMessage());
        }
    }
    /*@PutMapping("/updateRdvRecommande/{id}")
    public ResponseEntity<?> updateRdvRecommande(@PathVariable Long id, @RequestParam boolean rdvRecommande) {
        try {
            DossierMedical dossier = idossierMedicalServices.retrieveDossier(id);
            dossier.setRdvRecommande(rdvRecommande);
            DossierMedical updated = idossierMedicalServices.updateDossier(dossier);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dossier non trouvé : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }*/
    @PutMapping("/updateRdvRecommande/{id}")
    public ResponseEntity<?> updateRdvRecommande(@PathVariable Long id, @RequestParam boolean rdvRecommande) {
        try {
            DossierMedical updated = idossierMedicalServices.updateRdvRecommande(id, rdvRecommande);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dossier non trouvé : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour : " + e.getMessage());
        }
    }

    // Ajoutez ces endpoints à votre controller existant
    @GetMapping("/retrieveArchivedDossiers")
    public ResponseEntity<List<DossierMedical>> getArchivedDossiers() {
        List<DossierMedical> dossiers = idossierMedicalServices.retrieveArchivedDossiers();
        return ResponseEntity.ok(dossiers);
    }

    @PutMapping("/restoreDossier/{id}")
    public ResponseEntity<?> restoreDossier(@PathVariable Long id) {
        try {
            DossierMedical restoredDossier = idossierMedicalServices.restoreDossier(id);
            return ResponseEntity.ok(restoredDossier);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dossier non trouvé : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la restauration : " + e.getMessage());
        }
    }


}
