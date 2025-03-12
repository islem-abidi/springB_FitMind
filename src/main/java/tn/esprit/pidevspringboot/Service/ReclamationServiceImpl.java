package tn.esprit.pidevspringboot.Service;


import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReclamationServiceImpl implements IReclamationService {

    @Autowired
    private ReclamationRepository reclamationRepository;

    @Override
    public Reclamation addReclamation(Reclamation reclamation) {
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
        return reclamationRepository.findById(id)
                .map(existingReclamation -> {
                    existingReclamation.setTypeReclamation(reclamation.getTypeReclamation());
                    existingReclamation.setDateReclamation(reclamation.getDateReclamation());
                    existingReclamation.setDescription(reclamation.getDescription());
                    existingReclamation.setStatut(reclamation.getStatut());
                    existingReclamation.setDateResolution(reclamation.getDateResolution());
                    existingReclamation.setAction(reclamation.getAction());
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

