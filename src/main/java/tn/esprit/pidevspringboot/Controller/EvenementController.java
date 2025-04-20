package tn.esprit.pidevspringboot.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidevspringboot.Entities.Evenement.Evenement;
import tn.esprit.pidevspringboot.Repository.EvenementRepository;
import tn.esprit.pidevspringboot.Service.IEvenementService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EvenementController {

    @Autowired
    private final IEvenementService iEvenementService;

    // ✅ Dossier d’upload absolu
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/events/";

    public EvenementController(IEvenementService iEvenementService) {
        this.iEvenementService = iEvenementService;
    }

    @GetMapping
    public List<Evenement> getAllEvents() {
        return iEvenementService.retrieveAllEvenements();
    }

    @GetMapping("/{id}")
    public Evenement getEvent(@PathVariable("id") Long id) {
        return iEvenementService.retrieveEvenement(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Evenement> addOrUpdateEvent(
            @RequestPart("event") String eventJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            Evenement evenement = mapper.readValue(eventJson, Evenement.class);

            // ✅ Étape 1 : création OU mise à jour
            Evenement savedEvent = iEvenementService.addEvenement(evenement);

            // ✅ Étape 2 : upload d'image
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = savedEvent.getIdEvenement() + ".png";
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                savedEvent.setImage(filename);
                savedEvent = iEvenementService.addEvenement(savedEvent); // mise à jour avec image
            }

            return ResponseEntity.ok(savedEvent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Evenement> updateEvent(
            @PathVariable("id") Long id,
            @RequestPart("event") String eventJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

            Evenement evenement = mapper.readValue(eventJson, Evenement.class);
            evenement.setIdEvenement(id); // Très important !

            Evenement updatedEvent = iEvenementService.updateEvenement(evenement);

            // Gérer le fichier image si fourni
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String filename = updatedEvent.getIdEvenement() + ".png";
                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                updatedEvent.setImage(filename);
                updatedEvent = iEvenementService.updateEvenement(updatedEvent);
            }

            return ResponseEntity.ok(updatedEvent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long id) {
        iEvenementService.deleteEvenement(id);
    }

    // ✅ Endpoint pour accéder à une image
    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        Path imagePath = Paths.get(uploadDir, id + ".png");

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] image = Files.readAllBytes(imagePath);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + ".png\"")
                .body(image);
    }
    @GetMapping("/recommendation/{idUser}")
    public List<Evenement> getRecommendations(@PathVariable Long idUser) {
        return iEvenementService.getRecommandations(idUser);
    }


}
