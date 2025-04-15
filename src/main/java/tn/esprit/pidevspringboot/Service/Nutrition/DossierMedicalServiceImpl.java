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

        // Vérifier si l'IMC est déjà calculé et présent dans l'objet DossierMedical
        // Ici, on vérifie si l'IMC est 0 ou si les données nécessaires (poids, taille) sont présentes.
        if (dossier.getImc() == 0 && dossier.getPoids() > 0 && dossier.getTailles() > 0) {
            // Calcul de l'IMC si les données sont présentes
            double poids = dossier.getPoids();
            double taille = dossier.getTailles();

            if (taille > 0) {
                // Calculer l'IMC
                double imc = poids / Math.pow(taille / 100, 2);
                // Convertir l'IMC en float avant de le stocker
                dossier.setImc((float) imc);  // Stocker l'IMC calculé dans l'objet dossier
            }
        }

        return dossier;
    }




    /*@Override
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
    }*/
    private float calculIMC(float poids, float tailleCm) {
        float tailleM = tailleCm / 100f;
        return poids / (tailleM * tailleM);
    }

    @Override
    public DossierMedical addDossier(DossierMedical dossierMedical) {
        if (dossierMedical.getTailles() <= 0 || dossierMedical.getPoids() <= 0) {
            throw new IllegalArgumentException("Taille ou poids invalides pour calculer l'IMC.");
        }

        float imc = calculIMC(dossierMedical.getPoids(), dossierMedical.getTailles());
        dossierMedical.setImc(imc);

        return dossierMedicalRepository.save(dossierMedical);
    }



    @Override
    public DossierMedical updateDossier(DossierMedical dossierMedical) {
        // Vérifie que le dossier existe
        DossierMedical existingDossier = dossierMedicalRepository.findById(dossierMedical.getIdDossier())
                .orElseThrow(() -> new IllegalArgumentException("Dossier médical non trouvé."));

        // 🔒 Empêche la modification si le dossier est archivé
        if (Boolean.TRUE.equals(existingDossier.getArchived())) {
            throw new IllegalArgumentException("Le dossier est archivé et ne peut pas être modifié.");
        }

        // Mise à jour des champs
        existingDossier.setMaladies(dossierMedical.getMaladies());
        existingDossier.setObjectifSante(dossierMedical.getObjectifSante());
        existingDossier.setTraitements(dossierMedical.getTraitements());
        existingDossier.setTailles(dossierMedical.getTailles());
        existingDossier.setPoids(dossierMedical.getPoids());
        existingDossier.setGroupeSanguin(dossierMedical.getGroupeSanguin());
        existingDossier.setAllergies(dossierMedical.getAllergies());

        // 🔄 Recalcul automatique de l’IMC
        if (dossierMedical.getTailles() > 0 && dossierMedical.getPoids() > 0) {
            float imc = calculIMC(dossierMedical.getPoids(), dossierMedical.getTailles());
            existingDossier.setImc(imc);
        } else {
            throw new IllegalArgumentException("Taille ou poids invalides pour le recalcul de l'IMC.");
        }

        return dossierMedicalRepository.save(existingDossier);
    }



    @Override
    public DossierMedical archiveDossier(Long idDossier) {
        DossierMedical existingDossier = dossierMedicalRepository.findById(idDossier)
                .orElseThrow(() -> new IllegalArgumentException("Dossier médical non trouvé."));

        // Vérifie s’il est déjà archivé
        if (Boolean.TRUE.equals(existingDossier.getArchived())) {
            throw new IllegalStateException("Le dossier est déjà archivé.");
        }

        existingDossier.setArchived(true);
        return dossierMedicalRepository.save(existingDossier);
    }
    @Override
    public DossierMedical updateRdvRecommande(Long idDossier, boolean rdvRecommande) {
        DossierMedical dossier = retrieveDossier(idDossier);
        dossier.setRdvRecommande(rdvRecommande);
        return dossierMedicalRepository.save(dossier);
    }

}
