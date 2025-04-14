package tn.esprit.pidevspringboot.Controller.ActiviteSportive;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.userRepository;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.IActiviteServices;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor

@RequestMapping("/api/activites")
@CrossOrigin(origins = "http://localhost:4200")

@Tag(name= "gestion des activites")

public class ActiviteRestController {

    @Autowired
    IActiviteServices activiteServices;

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



}
