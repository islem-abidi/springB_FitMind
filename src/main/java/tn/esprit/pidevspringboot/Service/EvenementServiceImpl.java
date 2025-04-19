package tn.esprit.pidevspringboot.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.EtatEvent;
import tn.esprit.pidevspringboot.Entities.Evenement.Evenement;
import tn.esprit.pidevspringboot.Repository.EvenementRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EvenementServiceImpl implements IEvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Override
    public List<Evenement> retrieveAllEvenements() {
        List<Evenement> events = evenementRepository.findAll();
        for (Evenement event : events) {
            event.setEtatEvent(calculerEtat(event.getDateEvenement(), event.getDateFin()));
        }
        return events;
    }

    @Override
    public Evenement retrieveEvenement(Long id) {
        Evenement event = evenementRepository.findById(id).orElse(null);
        if (event != null) {
            event.setEtatEvent(calculerEtat(event.getDateEvenement(), event.getDateFin()));
        }
        return event;
    }

    public EtatEvent calculerEtat(LocalDateTime dateDebut, LocalDateTime dateFin) {
        LocalDateTime maintenant = LocalDateTime.now();

        if (dateDebut == null || dateFin == null) {
            return EtatEvent.A_VENIR;
        }

        if (maintenant.isBefore(dateDebut)) {
            return EtatEvent.A_VENIR;
        } else if (maintenant.isAfter(dateFin)) {
            return EtatEvent.PASSE;
        } else {
            return EtatEvent.EN_COURS;
        }
    }

    @Override
    public Evenement addEvenement(Evenement evenement) {
        // ✅ Préserver les inscriptions existantes lors de la modification
        if (evenement.getIdEvenement() != null) {
            Evenement existing = evenementRepository.findById(evenement.getIdEvenement()).orElse(null);
            if (existing != null && existing.getInscriptions() != null) {
                evenement.setInscriptions(existing.getInscriptions());
            }
        }

        // ✅ Calcul de l’état avant sauvegarde
        evenement.setEtatEvent(calculerEtat(evenement.getDateEvenement(), evenement.getDateFin()));
        return evenementRepository.save(evenement);
    }

    @Override
    public Evenement updateEvenement(Evenement evenement) {
        evenement.setEtatEvent(calculerEtat(evenement.getDateEvenement(), evenement.getDateFin()));
        return evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    // ✅ Mise à jour automatique des états chaque minute
    @Scheduled(cron = "0 */1 * * * *")
    public void mettreAJourEtatsEvenements() {
        List<Evenement> events = evenementRepository.findAll();
        for (Evenement event : events) {
            EtatEvent nouvelEtat = calculerEtat(event.getDateEvenement(), event.getDateFin());
            if (event.getEtatEvent() != nouvelEtat) {
                event.setEtatEvent(nouvelEtat);
                evenementRepository.save(event);
            }
        }
    }
}
