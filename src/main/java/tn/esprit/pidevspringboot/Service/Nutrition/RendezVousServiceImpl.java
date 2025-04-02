package tn.esprit.pidevspringboot.Service.Nutrition;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.Nutrition.RendezVousRepository;
import tn.esprit.pidevspringboot.Repository.User.UserRepository;


import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RendezVousServiceImpl implements IRendezVousServices {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RendezVous> retrieveAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    @Override
    public RendezVous retrieveRendezVous(Long idRendezVous) {
        return rendezVousRepository.findById(idRendezVous).orElse(null);
    }

    @Override
    public RendezVous addRendezVous(RendezVous rendezVous) {
        // Vérifier que l'étudiant est obligatoire
        if (rendezVous.getEtudiant() == null || rendezVous.getEtudiant().getIdUser() == null) {
            throw new IllegalArgumentException("L'étudiant est obligatoire.");
        }
        // Récupérer l'étudiant depuis la base de données
        User etudiant = userRepository.findById(rendezVous.getEtudiant().getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé."));
        // Vérifier si le nutritionniste est fourni (optionnel)
        if (rendezVous.getNutritioniste() != null && rendezVous.getNutritioniste().getIdUser() != null) {
            User nutritioniste = userRepository.findById(rendezVous.getNutritioniste().getIdUser())
                    .orElseThrow(() -> new IllegalArgumentException("Nutritionniste non trouvé."));
            rendezVous.setNutritioniste(nutritioniste);
        }
        // Vérification de la durée (entre 30 et 120 minutes)
        if (rendezVous.getDuree() < 30 || rendezVous.getDuree() > 120) {
            throw new IllegalArgumentException("La durée du rendez-vous doit être entre 30 et 120 minutes.");
        }
        // Vérification que la remarque n'est pas vide
        if (rendezVous.getRemarque() == null || rendezVous.getRemarque().isEmpty()) {
            throw new IllegalArgumentException("La remarque est obligatoire.");
        }
        // Affecter l'étudiant récupéré
        rendezVous.setEtudiant(etudiant);
        // Sauvegarder le rendez-vous dans la base de données
        return rendezVousRepository.save(rendezVous);
    }


    @Override
    public RendezVous updateRendezVous(RendezVous rendezVous) {
        if (rendezVous == null || rendezVous.getIdRendezVous() == null) {
            throw new IllegalArgumentException("Le RendezVous ou son ID ne peut pas être null.");
        }
        if (!rendezVousRepository.existsById(rendezVous.getIdRendezVous())) {
            throw new RuntimeException("RendezVous avec l'ID " + rendezVous.getIdRendezVous() + " non trouvé.");
        }
        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public RendezVous archiveRendezVous(Long idRendezVous) {
        RendezVous rendezVous = retrieveRendezVous(idRendezVous);
        if (rendezVous != null) {
            rendezVous.setArchived(true);
            return rendezVousRepository.save(rendezVous);
        } else {
            throw new RuntimeException("RendezVous avec l'ID " + idRendezVous + " non trouvé.");
        }
    }
}
