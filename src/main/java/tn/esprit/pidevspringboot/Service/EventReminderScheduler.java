package tn.esprit.pidevspringboot.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;
import tn.esprit.pidevspringboot.Entities.Evenement.StatutInscription;
import tn.esprit.pidevspringboot.Repository.InscriptionEvenementRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EventReminderScheduler {

    private final InscriptionEvenementRepository inscriptionRepo;
    private final MailService mailService;

    @Autowired
    public EventReminderScheduler(InscriptionEvenementRepository inscriptionRepo, MailService mailService) {
        this.inscriptionRepo = inscriptionRepo;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = 300000) // toutes les 5 minutes
    @Transactional
    public void sendEventReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<InscriptionEvenement> inscriptions = inscriptionRepo.findAll();

        for (InscriptionEvenement inscription : inscriptions) {
            if (inscription.getStatutInscription() == StatutInscription.CONFIRMEE) {
                LocalDateTime eventDate = inscription.getEvenement().getDateEvenement();

                if (eventDate.isAfter(now) &&
                        eventDate.isBefore(oneHourLater) &&
                        !inscription.isReminderSent()) {

                    String email = inscription.getUser().getEmail();
                    String subject = "\u23F0 Votre \u00e9v\u00e9nement commence dans 1 heure !";

                    String htmlContent = "<!DOCTYPE html>" +
                            "<html lang=\"fr\">" +
                            "<head>" +
                            "    <meta charset=\"UTF-8\">" +
                            "    <style>" +
                            "        body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; }" +
                            "        .container { max-width: 600px; margin: auto; background: #f7f7f7; padding: 20px; border-radius: 10px; }" +
                            "        .header { background-color: #ffc107; color: white; padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }" +
                            "        .header h1 { margin: 0; font-size: 24px; }" +
                            "        .content { background: white; padding: 20px; border: 1px solid #ddd; }" +
                            "        .highlight { color: #ff5722; font-weight: bold; }" +
                            "        .footer { font-size: 12px; text-align: center; color: #888; padding-top: 20px; }" +
                            "        .btn { display: inline-block; padding: 10px 20px; background-color: #009688; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }" +
                            "    </style>" +
                            "</head>" +
                            "<body>" +
                            "    <div class=\"container\">" +
                            "        <div class=\"header\">" +
                            "            <h1>\u23F0 Rappel d'\u00e9v\u00e9nement</h1>" +
                            "        </div>" +
                            "        <div class=\"content\">" +
                            "            <p>Bonjour <strong>" + inscription.getUser().getNom() + "</strong>,</p>" +
                            "            <p>Nous vous rappelons que l'\u00e9v\u00e9nement suivant commence dans <span class=\"highlight\">1 heure</span> :</p>" +
                            "            <ul>" +
                            "                <li><strong>\uD83D\uDCCC Titre :</strong> " + inscription.getEvenement().getTitre() + "</li>" +
                            "                <li><strong>\uD83D\uDCCD Lieu :</strong> " + inscription.getEvenement().getLieu() + "</li>" +
                            "                <li><strong>\uD83D\uDCC5 Date :</strong> " + inscription.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) + "</li>" +
                            "                <li><strong>\uD83D\uDD52 Heure :</strong> " + inscription.getEvenement().getDateEvenement().format(DateTimeFormatter.ofPattern("HH'h'mm")) + "</li>" +
                            "            </ul>" +
                            "        </div>" +
                            "        <div class=\"footer\">" +
                            "            © " + java.time.Year.now().getValue() + " FitMind. Tous droits r\u00e9serv\u00e9s." +
                            "        </div>" +
                            "    </div>" +
                            "</body>" +
                            "</html>";

                    mailService.sendSimpleEmail(email, subject, htmlContent);

                    inscription.setReminderSent(true);
                    inscriptionRepo.save(inscription);
                }
            }
        }
    }
}
