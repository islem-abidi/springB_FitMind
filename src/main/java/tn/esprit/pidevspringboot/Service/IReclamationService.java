package tn.esprit.pidevspringboot.Service;


import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;
import tn.esprit.pidevspringboot.dto.ReclamationResponseDTO;

import java.util.List;
import java.util.Optional;

public interface IReclamationService {
    Reclamation addReclamation(ReclamationRequest reclamation);
    List<Reclamation> getAllReclamations();
    Optional<Reclamation> getReclamationById(Integer id);
    void deleteReclamation(Integer id);
    List<Reclamation> getArchivedReclamations();
    Reclamation restoreReclamation(Integer id);
    Reclamation updateReclamationByAdmin(Integer id, ReclamationRequest dto);
     List<ReclamationResponseDTO> getAllReclamationsDTO();
}

