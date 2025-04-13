// src/main/java/tn/esprit/pidevspringboot/Service/ReclamationServiceImpl.java

package tn.esprit.pidevspringboot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;
import tn.esprit.pidevspringboot.dto.ReclamationResponseDTO;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationServiceImpl implements IReclamationService {

    @Autowired private BadWordFilterService badWordFilterService;
    @Autowired private ReclamationRepository reclamationRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public Reclamation addReclamation(ReclamationRequest dto) {
        if (dto.getIdUser() == null) {
            throw new IllegalArgumentException("idUser is required in this context");
        }

        User user = userRepository.findById(dto.getIdUser())
                .filter(u -> !u.isArchived() && u.isVerified())
                .orElseThrow(() -> new RuntimeException("User not found or not verified"));

        return createReclamation(user, dto);
    }

    public Reclamation createReclamation(User user, ReclamationRequest dto) {
        if (badWordFilterService.containsBadWords(dto.getDescription())) {
            throw new RuntimeException("Description contains inappropriate language.");
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setUser(user);
        reclamation.setDateReclamation(new Date());
        reclamation.setTypeReclamation(dto.getTypeReclamation());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut(Reclamation.StatutReclamation.En_Cours);
        reclamation.setArchived(false);

        return reclamationRepository.save(reclamation);
    }

    @Override
    public Reclamation updateReclamationByAdmin(Integer id, ReclamationRequest dto) {
        return reclamationRepository.findById(id)
                .map(existing -> {
                    if (dto.getStatut() != null) {
                        existing.setStatut(dto.getStatut());
                    }
                    if (dto.getDateResolution() != null) {
                        existing.setDateResolution(dto.getDateResolution());
                    }
                    return reclamationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Reclamation not found with id " + id));
    }

    @Override
    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findByArchivedFalse();
    }

    @Override
    public Optional<Reclamation> getReclamationById(Integer id) {
        return reclamationRepository.findById(id);
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
        return reclamationRepository.findByArchivedTrue();
    }

    @Override
    public Reclamation restoreReclamation(Integer id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with ID: " + id));
        reclamation.setArchived(false);
        return reclamationRepository.save(reclamation);
    }

    @Override
    public List<ReclamationResponseDTO> getAllReclamationsDTO() {
        return reclamationRepository.findAll().stream()
                .filter(r -> r.getUser() != null && !r.getUser().isArchived() && r.getUser().isVerified())
                .map(reclamation -> {
                    String nomUtilisateur = "";
                    User user = reclamation.getUser();
                    if (user != null) {
                        String prenom = user.getPrenom() != null ? user.getPrenom() : "";
                        String nom = user.getNom() != null ? user.getNom() : "";
                        nomUtilisateur = (prenom + " " + nom).trim();
                    }

                    return ReclamationResponseDTO.builder()
                            .id(reclamation.getIdReclamation())
                            .nomUtilisateur(nomUtilisateur)
                            .typeReclamation(reclamation.getTypeReclamation().name())
                            .description(reclamation.getDescription())
                            .statut(reclamation.getStatut().name())
                            .build();
                })
                .toList();
    }
}
