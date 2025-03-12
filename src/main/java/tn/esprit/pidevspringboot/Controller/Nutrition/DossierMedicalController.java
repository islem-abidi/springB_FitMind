package tn.esprit.pidevspringboot.Controller.Nutrition;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import tn.esprit.pidevspringboot.Service.Nutrition.IDossierMedicalServices;

import java.util.List;

@RestController
@RequestMapping("/dossierMedical")
@AllArgsConstructor
@Tag(name = "DossierMedical")

public class DossierMedicalController {

    @Autowired
    IDossierMedicalServices idossierMedicalServices;

    @GetMapping("/retrieveAllDossiers")
    public List<DossierMedical> getAllDossiers() {
        return idossierMedicalServices.retrieveAllDossiers();
    }

    @GetMapping("/retrieveDossier{id}")
    public DossierMedical getDossierById(@PathVariable Long id) {
        return idossierMedicalServices.retrieveDossier(id);
    }

    @PostMapping("/addDossier")
    public DossierMedical addDossier(@RequestBody DossierMedical dossierMedical) {
        return idossierMedicalServices.addDossier(dossierMedical);
    }

    @PutMapping("/updateDossier")
    public DossierMedical updateDossier(@RequestBody DossierMedical dossierMedical) {
        return idossierMedicalServices.updateDossier(dossierMedical);
    }
}
