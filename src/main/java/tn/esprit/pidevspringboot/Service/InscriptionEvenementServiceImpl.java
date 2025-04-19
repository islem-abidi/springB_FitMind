package tn.esprit.pidevspringboot.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;
import tn.esprit.pidevspringboot.Entities.Evenement.StatutInscription;
import tn.esprit.pidevspringboot.Repository.InscriptionEvenementRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InscriptionEvenementServiceImpl implements IInscriptionEvenementService {

    @Autowired
    private InscriptionEvenementRepository inscriptionEvenementRepository;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private MailService mailService;

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
        InscriptionEvenement saved = inscriptionEvenementRepository.save(inscriptionEvenement);

        // ✅ Vérifie si le statut est CONFIRMEE, alors envoie le mail
        if (saved.getStatutInscription() == StatutInscription.CONFIRMEE) {
            try {
                String email = saved.getUser().getEmail();
                String subject = "✅ Confirmation de votre inscription FitMind";

                // Construction d'un mail HTML plus professionnel
                String content = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <style>\n" +
                        "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333333; }\n" +
                        "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                        "        .header { background-color: #009e60; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }\n" +
                        "        .content { background-color: #f9f9f9; padding: 20px; border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; }\n" +
                        "        .footer { background-color: #468eb4; color: white; padding: 15px; text-align: center; border-radius: 0 0 5px 5px; font-size: 12px; }\n" +
                        "        .event-details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #009e60; }\n" +
                        "        .info-label { color: #468eb4; font-weight: bold; width: 100px; display: inline-block; }\n" +
                        "        .highlight { color: #f58220; font-weight: bold; }\n" +
                        "        .btn { display: inline-block; background-color: #009e60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px; }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Fit<span style=\"color: #d9edf7;\">Mind</span></h1>\n" +
                        "            <h2>Confirmation d'inscription</h2>\n" +
                        "        </div>\n" +
                        "        <div class=\"content\">\n" +
                        "            <p>Bonjour <strong>" + saved.getUser().getNom() + "</strong>,</p>\n" +
                        "            <p>Nous avons le plaisir de vous confirmer votre inscription à l'événement :</p>\n" +
                        "            \n" +
                        "            <div class=\"event-details\">\n" +
                        "                <h3>" + saved.getEvenement().getTitre() + "</h3>\n" +
                        "                <p><span class=\"info-label\">📍 Lieu :</span> " + saved.getEvenement().getLieu() + "</p>\n" +
                        "                <p><span class=\"info-label\">📅 Date :</span> " + saved.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "</p>\n" +
                        "                <p><span class=\"info-label\">⏰ Heure :</span> " + saved.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("HH'h'mm")) + "</p>\n" +
                        "                <p><span class=\"info-label\">🎫 N° Billet :</span> FM-" + String.format("%08d", saved.getIdInscription()) + "</p>\n" +
                        "            </div>\n" +
                        "            \n" +
                        "            <p class=\"highlight\">🔍 Important :</p>\n" +
                        "            <ul>\n" +
                        "                <li>Veuillez vous présenter 30 minutes avant le début de l'événement</li>\n" +
                        "                <li>N'oubliez pas de venir avec votre billet qui sera disponible dans votre espace personnel</li>\n" +
                        "                <li>Une pièce d'identité pourra vous être demandée</li>\n" +
                        "            </ul>\n" +
                        "            \n" +
                        "            <p style=\"margin-top: 30px;\">À très bientôt,</p>\n" +
                        "            <p><strong>L'équipe FitMind</strong></p>\n" +
                        "        </div>\n" +
                        "        <div class=\"footer\">\n" +
                        "            <p>FitMind - Bien-être du corps et de l'esprit</p>\n" +
                        "            <p>Contact: support@fitmind.com | +216 XX XXX XXX | www.fitmind.com</p>\n" +
                        "            <p>© " + java.time.Year.now().getValue() + " FitMind. Tous droits réservés.</p>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";

                System.out.println("✉️ Envoi d'e-mail à : " + email);
                mailService.sendSimpleEmail(email, subject, content);  // On utilise la méthode existante
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de l'envoi d'e-mail : " + e.getMessage());
            }
        }

        return saved;
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
        inscription.setQrCodeGenerated(true);

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

        return !inscription.isQrCodeGenerated();
    }

    @Override
    public List<InscriptionEvenement> findByEventId(Long idEvenement) {
        return inscriptionEvenementRepository.findByEvenementIdEvenement(idEvenement);
    }

    @Override
    public void updateInscriptionStatus(Long id, StatutInscription statutInscription) {
        InscriptionEvenement inscription = inscriptionEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setStatutInscription(statutInscription);

        if (statutInscription == StatutInscription.CONFIRMEE) {
            String email = inscription.getUser().getEmail();
            String subject = "✅ Confirmation de votre inscription FitMind";

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333333; }\n" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                    "        .header { background-color: #009e60; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }\n" +
                    "        .content { background-color: #f9f9f9; padding: 20px; border-left: 1px solid #e0e0e0; border-right: 1px solid #e0e0e0; }\n" +
                    "        .footer { background-color: #468eb4; color: white; padding: 15px; text-align: center; border-radius: 0 0 5px 5px; font-size: 12px; }\n" +
                    "        .event-details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #009e60; }\n" +
                    "        .info-label { color: #468eb4; font-weight: bold; width: 100px; display: inline-block; }\n" +
                    "        .highlight { color: #f58220; font-weight: bold; }\n" +
                    "        .btn { display: inline-block; background-color: #009e60; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Fit<span style=\"color: #d9edf7;\">Mind</span></h1>\n" +
                    "            <h2>Confirmation d'inscription</h2>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Bonjour <strong>" + inscription.getUser().getNom() + "</strong>,</p>\n" +
                    "            <p>Nous avons le plaisir de vous confirmer votre inscription à l'événement :</p>\n" +
                    "            <div class=\"event-details\">\n" +
                    "                <h3>" + inscription.getEvenement().getTitre() + "</h3>\n" +
                    "                <p><span class=\"info-label\">📍 Lieu :</span> " + inscription.getEvenement().getLieu() + "</p>\n" +
                    "                <p><span class=\"info-label\">📅 Date :</span> " + inscription.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "</p>\n" +
                    "                <p><span class=\"info-label\">⏰ Heure :</span> " + inscription.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("HH'h'mm")) + "</p>\n" +
                    "                <p><span class=\"info-label\">🎫 N° Billet :</span> FM-" + String.format("%08d", inscription.getIdInscription()) + "</p>\n" +
                    "            </div>\n" +
                    "            <p class=\"highlight\">🔍 Important :</p>\n" +
                    "            <ul>\n" +
                    "                <li>Veuillez vous présenter 30 minutes avant le début de l'événement</li>\n" +
                    "                <li>N'oubliez pas de venir avec votre billet</li>\n" +
                    "            </ul>\n" +
                    "            <p style=\"margin-top: 30px;\">À très bientôt,<br><strong>L'équipe FitMind</strong></p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p>FitMind - Bien-être du corps et de l'esprit</p>\n" +
                    "            <p>Contact: support@fitmind.com | +216 XX XXX XXX</p>\n" +
                    "            <p>© " + java.time.Year.now().getValue() + " FitMind. Tous droits réservés.</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            mailService.sendSimpleEmail(email, subject, htmlContent);
        }

        inscriptionEvenementRepository.save(inscription);
    }


    @Override
    public Long findInscriptionIdByUserAndEvent(Long idUser, Long idEvenement) {
        return inscriptionEvenementRepository.findInscriptionIdByUserAndEvent(idUser, idEvenement);
    }
}
