package tn.esprit.pidevspringboot.Controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.Service.BadWordFilterService;
import tn.esprit.pidevspringboot.Service.IReclamationService;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;

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
    private UserRepository userRepository;
    @Operation(summary = "Ajouter une réclamation", description = "Permet d'ajouter une nouvelle réclamation en spécifiant uniquement l'ID de l'utilisateur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addReclamation(@RequestBody ReclamationRequest reclamationDTO) {
        if (badWordFilterService.containsBadWords(reclamationDTO.getDescription())) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ Description invalide : langage inapproprié détecté.");
        }

        User user = userRepository.findById(reclamationDTO.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(user);
        reclamation.setDateReclamation(reclamationDTO.getDateReclamation());
        reclamation.setTypeReclamation(reclamationDTO.getTypeReclamation());
        reclamation.setDescription(reclamationDTO.getDescription());
        reclamation.setStatut(reclamationDTO.getStatut());
        reclamation.setDateResolution(reclamationDTO.getDateResolution());

        return ResponseEntity.ok(reclamationService.addReclamation(reclamationDTO));
    }


    @Operation(summary = "afficher une réclamation", description = "Permet d'afficher toutes les reclamations existantes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation affiché avec succès"),
    })
    @GetMapping("/getall")
    public List<Reclamation> getAllReclamations() {
        return reclamationService.getAllReclamations();
    }
    @Operation(summary = "afficher une réclamation selon l'id ", description = "Permetd'afficher une réclamation en spécifiant l'ID ")

    @GetMapping("/get/{id}")
    public Optional<Reclamation> getReclamationById(@PathVariable Integer id) {
        return reclamationService.getReclamationById(id);
    }
    @Operation(summary = "modifier une réclamation", description = "Permet de modifier une nouvelle réclamation en spécifiant uniquement l'ID .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReclamation(@PathVariable Integer id, @RequestBody ReclamationRequest reclamationDTO) {
        if (badWordFilterService.containsBadWords(reclamationDTO.getDescription())) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ Description invalide : langage inapproprié détecté.");
        }

        User user = userRepository.findById(reclamationDTO.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + reclamationDTO.getIdUser()));

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(user);
        reclamation.setDateReclamation(reclamationDTO.getDateReclamation());
        reclamation.setTypeReclamation(reclamationDTO.getTypeReclamation());
        reclamation.setDescription(reclamationDTO.getDescription());
        reclamation.setStatut(reclamationDTO.getStatut());
        reclamation.setDateResolution(reclamationDTO.getDateResolution());

        return ResponseEntity.ok(reclamationService.updateReclamation(id, reclamation));
    }


    @Operation(summary = "supprimer une réclamation", description = "Permet de supprimer une réclamation en spécifiant l'ID .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur dans les données envoyées")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteReclamation(@PathVariable Integer id) {
        reclamationService.deleteReclamation(id);
    }
    @Operation(summary = "Liste des réclamations archivées", description = "Affiche toutes les réclamations archivées.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping("/archived")
    public List<Reclamation> getArchivedReclamations() {
        return reclamationService.getArchivedReclamations();
    }
    @Operation(summary = "Restaurer une réclamation", description = "Restaure une réclamation archivée en spécifiant l'ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Réclamation restaurée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réclamation non trouvée")
    })
    @PutMapping("/restore/{id}")
    public Reclamation restoreReclamation(@PathVariable Integer id) {
        return reclamationService.restoreReclamation(id);
    }


}

