package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ActiviteRepository;
import tn.esprit.pidevspringboot.Repository.userRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActiviteServiceImpl implements IActiviteServices {
    @Autowired
    ActiviteRepository activiteRepository;
    @Autowired
    userRepository userRepository ;
    @Override
    public List<Activite> readAllActivite() {
        return activiteRepository.findAll();
    }

    @Override
    public Activite readActivitee(long idA) {
        return activiteRepository.findById(idA).get();
    }

    @Override
    public Activite addActivite(Activite activite) {

        if (activite.getUser() != null && !activite.getUser().isEmpty()) {
            Set<User> fullUsers = activite.getUser().stream()
                    .map(u -> userRepository.findById(u.getId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            activite.setUser(fullUsers);

            // ✅ Mise à jour de la relation inverse : ajouter l'activité à chaque user
            for (User user : fullUsers) {
                if (user.getActivite() == null) {
                    user.setActivite(new HashSet<>());
                }
                user.getActivite().add(activite);
            }
        }
        return activiteRepository.save(activite);
    }

    @Override
    public Activite updateActivite(Activite activite) {
        return activiteRepository.save(activite);
    }

    @Override
    public void deleteActivite(long idA) {
        activiteRepository.deleteById(idA);
    }


}
