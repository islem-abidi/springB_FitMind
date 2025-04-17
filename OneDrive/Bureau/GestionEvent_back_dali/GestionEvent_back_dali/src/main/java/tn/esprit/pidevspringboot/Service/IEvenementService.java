package tn.esprit.pidevspringboot.Service;

import tn.esprit.pidevspringboot.Entities.Evenement.Evenement;
import java.util.List;

public interface IEvenementService {
    List<Evenement> retrieveAllEvenements();
    Evenement retrieveEvenement(Long id);
    Evenement addEvenement(Evenement evenement);
    Evenement updateEvenement(Evenement evenement); // ✅ ajoutez cette ligne

    void deleteEvenement(Long id);
}