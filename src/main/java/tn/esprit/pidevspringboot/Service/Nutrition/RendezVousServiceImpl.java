package tn.esprit.pidevspringboot.Service.Nutrition;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.Nutrition.RendezVousRepository;
import tn.esprit.pidevspringboot.Repository.User.UserRepository;

import java.util.List;

@Service
public class RendezVousServiceImpl implements IRendezVousServices {

    @Autowired
    RendezVousRepository rendezVousRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public List<RendezVous> retrieveAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    @Override
    public RendezVous retrieveRendezVous(Long idRendezVous) {
        return rendezVousRepository.findById(idRendezVous)
                .orElseThrow(() -> new IllegalArgumentException("Rendez-vous non trouvé avec l'ID: " + idRendezVous));
    }

    @Override
    public RendezVous addRendezVous(RendezVous rendezVous) {
        // Verify if the student (etudiant) exists
        User etudiant = userRepository.findById(rendezVous.getEtudiant().getIdUser())
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Associate the student with the appointment
        rendezVous.setEtudiant(etudiant);

        // Check if a nutritionist is provided
        if (rendezVous.getNutritioniste() != null) {
            User nutritioniste = userRepository.findById(rendezVous.getNutritioniste().getIdUser())
                    .orElseThrow(() -> new RuntimeException("Nutritionniste non trouvé"));
            rendezVous.setNutritioniste(nutritioniste);
        }

        // Save the appointment to the database
        return rendezVousRepository.save(rendezVous);
    }



    @Override
    public RendezVous updateRendezVous(RendezVous rendezVous) {
        if (rendezVous == null || rendezVous.getIdRendezVous() == null) {
            throw new IllegalArgumentException("Le rendez-vous ou son ID ne peut pas être null.");
        }

        // Correction ici : on utilise existsById pour vérifier l'existence du rendez-vous
        if (!rendezVousRepository.existsById(rendezVous.getIdRendezVous())) {
            throw new IllegalArgumentException("Impossible de mettre à jour : rendez-vous inexistant.");
        }

        return rendezVousRepository.save(rendezVous);
    }

    @Override
    public RendezVous archiveRendezVous(Long idRendezVous) {
        RendezVous rendezVous = retrieveRendezVous(idRendezVous);
        // Marquer le rendez-vous comme archivé
        rendezVous.setArchived(true);
        return rendezVousRepository.save(rendezVous);
    }
}
