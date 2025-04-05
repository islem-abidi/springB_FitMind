package tn.esprit.pidevspringboot.Service.Nutrition;

import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import tn.esprit.pidevspringboot.Entities.User.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Repository.Nutrition.DossierMedicalRepository;
import tn.esprit.pidevspringboot.Repository.User.UserRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DossierMedicalServiceImpl implements IDossierMedicalServices {

    @Autowired
    private DossierMedicalRepository dossierMedicalRepository;

    @Autowired
    private UserRepository userRepository;  // Pour récupérer l'utilisateur

    @Override
    public List<DossierMedical> retrieveAllDossiers() {
        return dossierMedicalRepository.findByArchivedFalse();
    }


    @Override
    public DossierMedical retrieveDossier(Long idDossier) {
        DossierMedical dossier = dossierMedicalRepository.findById(idDossier)
                .orElseThrow(() -> new RuntimeException("Dossier non trouvé avec l'ID : " + idDossier));

        if (Boolean.TRUE.equals(dossier.getArchived())) {
            throw new RuntimeException("Ce dossier est archivé et ne peut pas être consulté.");
        }

        return dossier;
    }


    @Override
    public DossierMedical addDossier(DossierMedical dossierMedical) {
        // Vérification que l'utilisateur est valide
        if (dossierMedical.getUser() == null || dossierMedical.getUser().getIdUser() == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }

        // Récupération de l'utilisateur depuis la base de données
        User user = userRepository.findById(dossierMedical.getUser().getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));

        // Vérification du rôle
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Le rôle de l'utilisateur ne peut pas être null.");
        }

        // Vérification que l'utilisateur n'a pas déjà un dossier médical
        if (user.getRole().equals("etudiant")) {
            Optional<DossierMedical> existingDossier = dossierMedicalRepository.findByUser(user);
            if (existingDossier.isPresent()) {
                throw new IllegalArgumentException("L'utilisateur a déjà un dossier médical.");
            }
        }
        // Mise à jour de l'utilisateur dans l'objet dossier médical
        dossierMedical.setUser(user);
        // Vérification des champs obligatoires
        if (dossierMedical.getMaladies() == null || dossierMedical.getMaladies().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'maladies' est obligatoire.");
        }

        if (dossierMedical.getObjectifSante() == null || dossierMedical.getObjectifSante().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'objectifSante' est obligatoire.");
        }
        if (dossierMedical.getTraitements() == null || dossierMedical.getTraitements().isEmpty()) {
            throw new IllegalArgumentException("Le champ 'traitements' est obligatoire.");
        }
        if (dossierMedical.getTailles() <= 0) {
            throw new IllegalArgumentException("La taille doit être positive.");
        }
        if (dossierMedical.getPoids() <= 0) {
            throw new IllegalArgumentException("Le poids doit être positif.");
        }

        return dossierMedicalRepository.save(dossierMedical);
    }

    @Override
    public DossierMedical updateDossier(DossierMedical dossierMedical) {
        if (dossierMedical == null || dossierMedical.getIdDossier() == null) {
            throw new IllegalArgumentException("Le DossierMedical ou son ID ne peut pas être null.");
        }

        if (!dossierMedicalRepository.existsById(dossierMedical.getIdDossier())) {
            throw new RuntimeException("DossierMedical avec l'ID " + dossierMedical.getIdDossier() + " non trouvé.");
        }

        return dossierMedicalRepository.save(dossierMedical);
    }

    @Override
    public DossierMedical archiveDossier(Long idDossier) {
        DossierMedical dossierMedical = retrieveDossier(idDossier);

        if (dossierMedical != null) {
            dossierMedical.setArchived(true); // Marquer comme archivé
            return dossierMedicalRepository.save(dossierMedical); // Sauvegarder les changements
        } else {
            throw new RuntimeException("DossierMedical avec l'ID " + idDossier + " non trouvé.");
        }
    }
}
