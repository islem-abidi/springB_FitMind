package tn.esprit.pidevspringboot.Service;

import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;
import tn.esprit.pidevspringboot.Entities.Evenement.StatutInscription;

import java.util.List;

public interface IInscriptionEvenementService {
    List<InscriptionEvenement> retrieveAllInscriptions();
    InscriptionEvenement retrieveInscription(Long id);
    InscriptionEvenement addInscription(InscriptionEvenement inscriptionEvenement);
    void deleteInscription(Long id);
    boolean deleteByUserAndEvent(Long idUser, Long idEvenement);
    // Dans IInscriptionEvenementService.java
    List<InscriptionEvenement> findByEventId(Long idEvenement);
    void updateInscriptionStatus(Long id, StatutInscription statutInscription);
    InscriptionEvenement generateQRCode(Long inscriptionId) throws Exception;
    boolean canCancelInscription(Long inscriptionId);
    Long findInscriptionIdByUserAndEvent(Long idUser, Long idEvenement);

}
