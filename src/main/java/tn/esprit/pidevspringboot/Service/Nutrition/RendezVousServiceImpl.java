package tn.esprit.pidevspringboot.Service.Nutrition;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import tn.esprit.pidevspringboot.Entities.Nutrition.StatutRendezVous;
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
        return rendezVousRepository.findByArchivedFalse();
    }


    @Override
    public RendezVous retrieveRendezVous(Long idRendezVous) {
        return rendezVousRepository.findById(idRendezVous).orElse(null);
    }

    @Override
    public RendezVous addRendezVous(RendezVous rendezVous) {
        validateRendezVousData(rendezVous);

        User etudiant = userRepository.findById(rendezVous.getEtudiant().getIdUser())
                .orElseThrow(() -> new IllegalArgumentException("Étudiant non trouvé."));

        rendezVous.setEtudiant(etudiant);

        if (rendezVous.getNutritioniste() != null && rendezVous.getNutritioniste().getIdUser() != null) {
            User nutritioniste = userRepository.findById(rendezVous.getNutritioniste().getIdUser())
                    .orElseThrow(() -> new IllegalArgumentException("Nutritionniste non trouvé."));
            rendezVous.setNutritioniste(nutritioniste);
        }

        return rendezVousRepository.save(rendezVous);
    }

    private void validateRendezVousData(RendezVous rendezVous) {
        if (rendezVous.getEtudiant() == null || rendezVous.getEtudiant().getIdUser() == null)
            throw new IllegalArgumentException("L'étudiant est obligatoire.");
        if (rendezVous.getDateHeure() == null)
            throw new IllegalArgumentException("La date/heure est obligatoire.");
        if (rendezVous.getDuree() < 30 || rendezVous.getDuree() > 120)
            throw new IllegalArgumentException("La durée doit être entre 30 et 120 minutes.");
        if (rendezVous.getRemarque() == null || rendezVous.getRemarque().isEmpty())
            throw new IllegalArgumentException("La remarque est obligatoire.");
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

    @Override
    public void updateStatutRendezVous(Long id, String statut) {
        // Chercher le rendez-vous par son ID
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec ID : " + id));

        try {
            // Tenter de convertir le statut en une valeur de l'énumération
            StatutRendezVous statutEnum = StatutRendezVous.valueOf(statut);
            rendezVous.setStatut(statutEnum); // Mettre à jour le statut
            rendezVousRepository.save(rendezVous); // Sauvegarder le rendez-vous mis à jour
        } catch (IllegalArgumentException e) {
            // Si le statut est invalide, lever une exception avec un message spécifique
            throw new IllegalArgumentException("Statut invalide : " + statut);
        }
    }

}