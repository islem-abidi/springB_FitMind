package tn.esprit.pidevspringboot.Service;


import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;

import java.util.List;
import java.util.Optional;

@Service
public class ReclamationServiceImpl implements IReclamationService {
    @Autowired
    private BadWordFilterService badWordFilterService;
    @Autowired
    private ReclamationRepository reclamationRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Reclamation addReclamation(ReclamationRequest dto) {
        if (badWordFilterService.containsBadWords(dto.getDescription())) {
            throw new RuntimeException("Description contains inappropriate language.");
        }

        User user = userRepository.findById(dto.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(user);
        reclamation.setDateReclamation(dto.getDateReclamation());
        reclamation.setTypeReclamation(dto.getTypeReclamation());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut(dto.getStatut());
        reclamation.setDateResolution(dto.getDateResolution());

        return reclamationRepository.save(reclamation);
    }
    @Override
    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findByArchivedFalse(); // Ne renvoie que les non archivées
    }


    @Override
    public Optional<Reclamation> getReclamationById(Integer id) {
        return reclamationRepository.findById(id);
    }

    @Override
    public Reclamation updateReclamation(Integer id, Reclamation reclamation) {
        if (badWordFilterService.containsBadWords(reclamation.getDescription())) {
            throw new RuntimeException("Description contains inappropriate language.");
        }
        return reclamationRepository.findById(id)
                .map(existingReclamation -> {
                    existingReclamation.setTypeReclamation(reclamation.getTypeReclamation());
                    existingReclamation.setDateReclamation(reclamation.getDateReclamation());
                    existingReclamation.setDescription(reclamation.getDescription());
                    existingReclamation.setStatut(reclamation.getStatut());
                    existingReclamation.setDateResolution(reclamation.getDateResolution());
                    return reclamationRepository.save(existingReclamation);
                }).orElseThrow(() -> new RuntimeException("Reclamation not found with id " + id));
    }

    @Override
    public void deleteReclamation(Integer id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with ID: " + id));

        reclamation.setArchived(true);
        reclamationRepository.save(reclamation);
    }
    @Override
    public List<Reclamation> getArchivedReclamations() {
        return reclamationRepository.findByArchivedFalse();
    }

    @Override
    public Reclamation restoreReclamation(Integer id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with ID: " + id));

        // Restaurer la réclamation en la rendant active
        reclamation.setArchived(false);
        return reclamationRepository.save(reclamation);
    }



}

