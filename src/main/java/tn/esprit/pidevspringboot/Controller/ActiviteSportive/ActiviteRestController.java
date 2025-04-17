package tn.esprit.pidevspringboot.Controller.ActiviteSportive;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ActiviteRepository;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ReservationRepository;
import tn.esprit.pidevspringboot.Repository.userRepository;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.IActiviteServices;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200") // 💡 assure le fonctionnement même si config globale ne passe pas
@RestController
@AllArgsConstructor
@RequestMapping("/api/activites")
@Tag(name= "gestion des activites")
public class ActiviteRestController {

    @Autowired
    IActiviteServices activiteServices;
@Autowired
ReservationRepository reservationRepository;
@Autowired
ActiviteRepository activiteRepository;

    @PostMapping(value = "/uploadImage/{idA}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@PathVariable("idA") long idA, @RequestParam("image") MultipartFile imageFile) {
        try {
            Activite activite = activiteServices.readActivitee(idA);
            activite.setImage(imageFile.getBytes());
            activiteServices.updateActivite(activite);
            return ResponseEntity.ok(Collections.singletonMap("message", "Image uploadée avec succès !"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Erreur lors de l'upload de l'image."));
        }
    }
    @GetMapping("/image/{idA}")
    public ResponseEntity<byte[]> getImage(@PathVariable("idA") long idA) {
        Activite activite = activiteServices.readActivitee(idA);

        if (activite.getImage() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // ou IMAGE_PNG selon ton format
            return new ResponseEntity<>(activite.getImage(), headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/addActiviteWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Activite> ajouterActiviteAvecImage(
            @RequestPart("activite") Activite activite,
            @RequestPart("image") MultipartFile imageFile
    ) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                activite.setImage(imageFile.getBytes());
            }
            return ResponseEntity.ok(activiteServices.addActivite(activite));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/readAllActivite")
    public List<Activite> afficherActivite () {
        return activiteServices.readAllActivite();
    }

    @GetMapping("/readActivite/{idA}")
    public Activite afficherActiviteById(@PathVariable("idA") long id){
        return activiteServices.readActivitee(id);
    }

    @PostMapping("/addActivite")
    public ResponseEntity<?> ajouterActivite(@Valid @RequestBody Activite activite, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        return ResponseEntity.ok(activiteServices.addActivite(activite));
    }
    @PutMapping("/updateActivite")
    public Activite updateActivite(@Valid @RequestBody Activite activite){
        return activiteServices.updateActivite(activite);
    }

    @DeleteMapping("/deleteActivite/{idA}")
    public void deleteActiviteById(@PathVariable("idA")long  idA){
        activiteServices.deleteActivite(idA);
    }

    @GetMapping("/tendance")
    public List<Activite> getActivitesAvecReservationsSemaine() {
        return activiteServices.getActivitesTendances();
    }



}
