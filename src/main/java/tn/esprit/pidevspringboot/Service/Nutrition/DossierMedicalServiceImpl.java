package tn.esprit.pidevspringboot.Service.Nutrition;

import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Repository.Nutrition.DossierMedicalRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class DossierMedicalServiceImpl implements IDossierMedicalServices {
    @Autowired
    private DossierMedicalRepository dossierMedicalRepository;

    @Override
    public List<DossierMedical> retrieveAllDossiers() {
        return dossierMedicalRepository.findAll();
    }

    @Override
    public DossierMedical retrieveDossier(Long idDossier) {
        return dossierMedicalRepository.findById(idDossier).orElse(null);
    }

    @Override
    public DossierMedical addDossier(DossierMedical dossierMedical) {
        return dossierMedicalRepository.save(dossierMedical);
    }

    @Override
    public DossierMedical updateDossier(DossierMedical dossierMedical) {
        // Vérifier si le DossierMedical existe déjà en base
        if (dossierMedicalRepository.existsById(dossierMedical.getIdDossier())) {
            // Si le DossierMedical existe, on met à jour les données
            return dossierMedicalRepository.save(dossierMedical);
        } else {
            // Si le DossierMedical n'existe pas, retourner une exception ou autre réponse appropriée
            throw new RuntimeException("DossierMedical with id " + dossierMedical.getIdDossier() + " not found");
        }
    }



}
