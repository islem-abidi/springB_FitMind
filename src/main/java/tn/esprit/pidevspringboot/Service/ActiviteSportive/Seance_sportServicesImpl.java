package tn.esprit.pidevspringboot.Service.ActiviteSportive;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Seance_sport;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ActiviteRepository;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.Seance_sportRepository;

import java.util.List;
@Service
@AllArgsConstructor
public class Seance_sportServicesImpl implements ISeance_sportServices {

    @Autowired
    Seance_sportRepository seance_sportRepository;

    @Autowired
    ActiviteRepository activiteRepository;
    @Override
    public List<Seance_sport> readAllSeance_sport() {
        return seance_sportRepository.findAll();
    }

    @Override
    public Seance_sport readSeance_sport(long idS) {
        return seance_sportRepository.findById(idS).get();
    }

    @Override
    public Seance_sport addSeance_sport(Seance_sport seance_sport) {
        if (seance_sport.getActivite() == null ) {
            throw new RuntimeException("L'activité est obligatoire.");
        }

        // 💡 Recharge l'objet Activite JPA complet depuis l'ID
        Long idActivite = seance_sport.getActivite().getId();
        Activite activite = activiteRepository.findById(idActivite)
                .orElseThrow(() -> new RuntimeException("Activité introuvable : " + idActivite));

        seance_sport.setActivite(activite); // 👈 JPA attend un objet managé
        return seance_sportRepository.save(seance_sport);
    }


    @Override
    public Seance_sport updateSeance_sport(Seance_sport seance_sport) {
        return seance_sportRepository.save(seance_sport);
    }

    @Override
    public void deleteSeance_sport(long idS) {
        seance_sportRepository.deleteById(idS);
    }
    @Override
    public List<Seance_sport> getSeancesByActivite(Long id) {
        List<Seance_sport> result = seance_sportRepository.findByActiviteId(id);
        System.out.println("Résultat pour activite " + id + " = " + result.size() + " séance(s)");
        return result;
    }

}
