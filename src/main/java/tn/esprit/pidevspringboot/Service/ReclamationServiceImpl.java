
package tn.esprit.pidevspringboot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import tn.esprit.pidevspringboot.Entities.User.Roletype;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ReclamationRepository;
import tn.esprit.pidevspringboot.Repository.UserRepository;
import tn.esprit.pidevspringboot.dto.ReclamationRequest;
import tn.esprit.pidevspringboot.dto.ReclamationResponse;
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
    public Reclamation createReclamation(User etudiant, ReclamationRequest dto) {
        if (badWordFilterService.containsBadWords(dto.getDescription())) {
            throw new RuntimeException("❌ La description contient un langage inapproprié.");
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setEtudiant(etudiant);
        reclamation.setDateReclamation(new Date());
        reclamation.setTypeReclamation(dto.getTypeReclamation());
        reclamation.setDescription(dto.getDescription());
        reclamation.setStatut(Reclamation.StatutReclamation.En_Cours);
        reclamation.setArchived(false);

        return reclamationRepository.save(reclamation);
    }


    @Override
    public Reclamation addReclamation(ReclamationRequest dto) {

        if (badWordFilterService.containsBadWords(dto.getDescription())) {
            throw new RuntimeException("La description contient des mots interdits.");
        }

        Reclamation r = new Reclamation();
        r.setTypeReclamation(dto.getTypeReclamation());
        r.setDescription(dto.getDescription());
        r.setDateReclamation(new Date());
        r.setStatut(Reclamation.StatutReclamation.En_Cours);
        r.setArchived(false);

        return reclamationRepository.save(r);
    }


    @Override
    public Reclamation updateReclamationByAdmin(Integer id, ReclamationRequest dto, User admin) {
        return reclamationRepository.findById(id)
                .map(existing -> {
                    if (dto.getStatut() != null) {
                        existing.setStatut(dto.getStatut());
                    }
                    if (dto.getDateResolution() != null) {
                        existing.setDateResolution(dto.getDateResolution());
                    }
                    existing.setAdmin(admin);
                    return reclamationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Réclamation introuvable avec l'ID : " + id));
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
    public List<ReclamationResponseDTO> getAllReclamationsDTO(boolean includeArchivedOrUnverified) {
        List<Reclamation> all = includeArchivedOrUnverified
                ? reclamationRepository.findAll()
                : reclamationRepository.findByArchivedFalse();
        System.out.println("✔️ Reclamations chargées : " + all.size());

        return all.stream()
                .filter(r -> r.getEtudiant() != null)
                .filter(r -> includeArchivedOrUnverified || (r.getEtudiant().isEnabled() && !r.getEtudiant().isArchived()))
                .map(r -> {
                    User user = r.getEtudiant();
                    return ReclamationResponseDTO.builder()
                            .id(r.getIdReclamation())
                            .nomUtilisateur(user.getPrenom() + " " + user.getNom())
                            .typeReclamation(r.getTypeReclamation().name())
                            .description(r.getDescription())
                            .statut(r.getStatut().name())
                            .dateReclamation(r.getDateReclamation())
                            .build();
                }).toList();
    }

        private ReclamationResponse mapToReclamationResponse(Reclamation r) {
        ReclamationResponse dto = new ReclamationResponse();
        dto.setId(Long.valueOf(r.getIdReclamation()));
        dto.setType(r.getTypeReclamation().name());
        dto.setDescription(r.getDescription());
        dto.setStatut(r.getStatut().name());
        dto.setDateReclamation(r.getDateReclamation());
        dto.setDateResolution(r.getDateResolution());
        dto.setNomEtudiant(r.getEtudiant() != null ? r.getEtudiant().getUsername() : null);
        dto.setNomAdmin(r.getAdmin() != null ? r.getAdmin().getUsername() : null);
        return dto;
    }

    @Override
    public List<ReclamationResponse> getMesReclamationsResponse(User user) {
        return reclamationRepository.findByEtudiant_IdUser(user.getIdUser())
                .stream()
                .map(this::mapToReclamationResponse)
                .toList();
    }
}
