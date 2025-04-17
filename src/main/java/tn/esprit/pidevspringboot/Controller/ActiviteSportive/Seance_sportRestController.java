package tn.esprit.pidevspringboot.Controller.ActiviteSportive;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Seance_sport;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ActiviteRepository;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.Seance_sportRepository;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.ISeance_sportServices;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/Seance_sport")
@Tag(name= "gestion des séances de sport")
public class Seance_sportRestController {
    @Autowired
    ISeance_sportServices seance_sportServices;
    @Autowired
    Seance_sportRepository seance_sportRepository;
    @Autowired
    ActiviteRepository activiteRepository;
    @GetMapping("/readAllSeance_sport")
    public List<Seance_sport> afficherSeance_sport (){

        return seance_sportServices.readAllSeance_sport();
    }

    @GetMapping("/readSeance_sport/{idS}")
    public Seance_sport afficherSeance_sportById(@PathVariable("idS") long id){
        return seance_sportServices.readSeance_sport(id);
    }

    @GetMapping("/getSeancesByActivite/{id}")
    public List<Seance_sport> getSeancesByActivite(@PathVariable Long id) {
        return seance_sportServices.getSeancesByActivite(id);
    }

    @PostMapping("/addSeance_sport")
    public Seance_sport ajouterSeance_sport(@Valid @RequestBody Seance_sport seance_sport ){
       return seance_sportServices.addSeance_sport(seance_sport);
    }

    @PostMapping("/addSeance_sport/{activiteId}")
    public ResponseEntity<Seance_sport> addSeanceSport(
            @PathVariable Long activiteId,
            @RequestBody Seance_sport seance_sport) {

        System.out.println("➡️ Ajout de séance : " + seance_sport);
        System.out.println("🔗 Activité reçue : " + seance_sport.getActivite());

        Activite activite = activiteRepository.findById(activiteId)
                .orElseThrow(() -> new RuntimeException("Activité introuvable avec ID : " + activiteId));

        seance_sport.setActivite(activite);

        Seance_sport saved = seance_sportRepository.save(seance_sport);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/updateSeance_sport")
    public Seance_sport updateSeance_sport(@RequestBody Seance_sport seance_sport){
        return seance_sportServices.updateSeance_sport(seance_sport);
    }

    @DeleteMapping("/deleteSeance_sport/{idS}")
    public void deleteSeance_sportById(@PathVariable("idS")long  idS){
        seance_sportServices.deleteSeance_sport(idS);
    }

}
