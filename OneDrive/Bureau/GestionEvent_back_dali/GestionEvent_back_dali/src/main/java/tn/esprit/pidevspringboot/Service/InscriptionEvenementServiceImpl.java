package tn.esprit.pidevspringboot.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;
import tn.esprit.pidevspringboot.Entities.Evenement.StatutInscription;
import tn.esprit.pidevspringboot.Repository.InscriptionEvenementRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InscriptionEvenementServiceImpl implements IInscriptionEvenementService {

    @Autowired
    InscriptionEvenementRepository inscriptionEvenementRepository;
    @Autowired
    QRCodeService qrCodeService;
    @Override
    public List<InscriptionEvenement> retrieveAllInscriptions() {
        return inscriptionEvenementRepository.findAll();
    }

    @Override
    public InscriptionEvenement retrieveInscription(Long id) {
        return inscriptionEvenementRepository.findById(id).orElse(null);
    }

    @Override
    public InscriptionEvenement addInscription(InscriptionEvenement inscriptionEvenement) {
        return inscriptionEvenementRepository.save(inscriptionEvenement);
    }

    @Override
    public void deleteInscription(Long id) {
        inscriptionEvenementRepository.deleteById(id);
    }
    @Override
    public boolean deleteByUserAndEvent(Long idUser, Long idEvenement) {
        try {
            Optional<InscriptionEvenement> inscriptionOpt = inscriptionEvenementRepository.findByUserIdAndEventId(idUser, idEvenement);
            if (inscriptionOpt.isPresent()) {
                InscriptionEvenement inscription = inscriptionOpt.get();

                // Vérifier si le QR code a été généré
                if (inscription.isQrCodeGenerated()) {
                    throw new RuntimeException("Impossible d'annuler l'inscription : le QR code a déjà été généré");
                }

                inscriptionEvenementRepository.delete(inscription);
                return true;
            } else {
                System.err.println("❌ Aucune inscription trouvée pour l'utilisateur " + idUser + " et l'événement " + idEvenement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public InscriptionEvenement generateQRCode(Long id) throws Exception {
        InscriptionEvenement inscription = inscriptionEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        // ✅ 1. Générer le QR Code
        String qrCodePath = qrCodeService.generateQRCode(inscription);
        inscription.setQrCodePath(qrCodePath);
        inscription.setQrCodeGenerated(true); // si tu as ce champ

        // ✅ 2. Générer le billet PDF
        String ticketPdfPath = qrCodeService.generateTicketPDF(inscription, qrCodePath);
        inscription.setTicketPdfPath(ticketPdfPath);

        // ✅ 3. Sauvegarder les chemins
        return inscriptionEvenementRepository.save(inscription);
    }



    @Override
    public boolean canCancelInscription(Long inscriptionId) {
        InscriptionEvenement inscription = inscriptionEvenementRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        // Ne peut pas annuler si le QR code a été généré
        return !inscription.isQrCodeGenerated();
    }
    // Dans InscriptionEvenementServiceImpl.java
    @Override
    public List<InscriptionEvenement> findByEventId(Long idEvenement) {
        return inscriptionEvenementRepository.findByEvenementIdEvenement(idEvenement);
    }
    @Override
    public void updateInscriptionStatus(Long id, StatutInscription statutInscription) {
        InscriptionEvenement inscription = inscriptionEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setStatutInscription(statutInscription);
        inscriptionEvenementRepository.save(inscription);
    }

    @Override
    public Long findInscriptionIdByUserAndEvent(Long idUser, Long idEvenement) {
        return inscriptionEvenementRepository.findInscriptionIdByUserAndEvent(idUser, idEvenement);
    }
}
