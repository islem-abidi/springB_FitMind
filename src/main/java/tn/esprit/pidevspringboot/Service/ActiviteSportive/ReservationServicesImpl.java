package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Reservation;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Seance_sport;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ReservationRepository;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.Seance_sportRepository;
import tn.esprit.pidevspringboot.Repository.userRepository;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Status;

import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
public class ReservationServicesImpl implements IReservationServices {

    @Autowired
    EmailService emailService;

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    Seance_sportRepository seanceRepository;
    @Autowired
    userRepository userRepository ;

    @Override
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepository.findByUser_Id(userId);
    }
    @Override
    public Reservation ajouterAListAttente(Long idSeance, Long idUser) {
        Seance_sport seance = seanceRepository.findById(idSeance)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Reservation reservation = new Reservation();
        reservation.setSeance(seance);
        reservation.setUser(user);
        reservation.setDateReservation(new Date());
        reservation.setStatus(Status.EN_ATTENTE); // ✅ En attente au départ

        Reservation savedReservation = reservationRepository.save(reservation);

        // 📩 Email avec lien de confirmation
        String confirmationLink = "http://localhost:4200/activite-off?reservationId=" + savedReservation.getId_reservation();
        String subject = "🎯 Confirmation de votre réservation - " + seance.getActivite().getNomActivite();

        String body = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color:#2c3e50;'>Bonjour " + user.getNom() + ",</h2>" +
                "<p>Vous êtes inscrit(e) en <strong>liste d'attente</strong> pour l'activité <strong style='color:#2980b9;'>" +
                seance.getActivite().getNomActivite() + "</strong>.</p>" +
                "<p><strong>📅 Date :</strong> " + seance.getDateSeance() + "<br>" +
                "<strong>🕒 Heure :</strong> " + seance.getHeureDebut() + "<br>" +
                "<strong>📍 Lieu :</strong> " + seance.getLieu() + "</p>" +
                "<hr>" +
                "<p style='color:#e67e22;'>👉 Une place se libère ? Vous pouvez confirmer votre participation ici :</p>" +
                "<p><a href='" + confirmationLink + "' style='padding:10px 20px;background-color:#27ae60;color:white;text-decoration:none;border-radius:5px;'>Confirmer ma réservation</a></p>" +
                "<br><p>Merci et à bientôt 👋<br>L'équipe Activité Sportive</p>" +
                "</body></html>";


        emailService.sendConfirmationEmail(user.getEmail(), subject, body);

        return savedReservation;
    }


    @Override
    public String confirmerReservationParUtilisateur(Long reservationId) {
        System.out.println("🔔 Tentative de confirmation pour la réservation ID : " + reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    System.out.println("❌ Réservation introuvable.");
                    return new RuntimeException("Réservation introuvable");
                });

        if (reservation.getStatus() == Status.CONFIRMEE) {
            System.out.println("ℹ️ Réservation déjà confirmée.");
            return "Réservation déjà confirmée.";
        }

        Seance_sport seance = reservation.getSeance();

        if (seance.getCapaciteDispo() <= 0) {
            System.out.println("❌ Plus de places disponibles pour confirmer la réservation.");
            return "Erreur : Plus de places disponibles.";
        }

        // ✅ Confirmation
        reservation.setStatus(Status.CONFIRMEE);
        reservationRepository.save(reservation);

        seance.setCapaciteDispo(seance.getCapaciteDispo() - 1);
        seanceRepository.save(seance);

        System.out.println("✅ Réservation confirmée avec succès pour l'utilisateur : " + reservation.getUser().getNom());
        System.out.println("📤 Préparation de l'envoi du 2e e-mail à : " + reservation.getUser().getEmail());


        // Email de confirmation
        String subject = "🎉 Réservation Confirmée - " + seance.getActivite().getNomActivite();

        String body = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color:#2c3e50;'>Bonjour " + reservation.getUser().getNom() + ",</h2>" +
                "<p>👏 Votre réservation pour l'activité <strong style='color:#2980b9;'>" + seance.getActivite().getNomActivite() + "</strong> est maintenant <strong style='color:green;'>confirmée</strong>.</p>" +
                "<p><strong>📅 Date :</strong> " + seance.getDateSeance() + "<br>" +
                "<strong>🕒 Heure :</strong> " + seance.getHeureDebut() + "<br>" +
                "<strong>📍 Lieu :</strong> " + seance.getLieu() + "</p>" +
                "<hr>" +
                "<p style='font-style: italic; color: #555;'>Merci de votre confiance ! Préparez-vous à bouger ! 💪</p>" +
                "<p>L'équipe <strong>Activité Sportive</strong></p>" +
                "</body></html>";

        emailService.sendConfirmationEmail(
                reservation.getUser().getEmail(),
                subject,
                body
        );


        return "Réservation confirmée avec succès.";
    }



    @Override
    public List<Reservation> getListeAttente(Long seanceId) {
        return reservationRepository.findBySeance_IdAndStatusOrderByDateReservationAsc(seanceId, Status.EN_ATTENTE);
    }

    @Override
    public void confirmerProchaineReservation(Long seanceId) {
        List<Reservation> enAttente = reservationRepository.findBySeance_IdAndStatusOrderByDateReservationAsc(seanceId, Status.EN_ATTENTE);
        if (!enAttente.isEmpty()) {
            Reservation reservation = enAttente.get(0);
            reservation.setStatus(Status.CONFIRMEE);
            reservationRepository.save(reservation);

            // TODO: notifier l'utilisateur (email / sms / etc.)
        }
    }


    @Override
    public String annulerReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        if (reservation.getStatus() == Status.ANNULEE) {
            return "Déjà annulée.";
        }

        reservation.setStatus(Status.ANNULEE);
        reservationRepository.save(reservation);

        // Remettre une place dispo
        Seance_sport seance = reservation.getSeance();
        seance.setCapaciteDispo(seance.getCapaciteDispo() + 1);
        seanceRepository.save(seance);

        // (Optionnel) Envoyer un email à l’utilisateur
        emailService.sendConfirmationEmail(
                reservation.getUser().getEmail(),
                "Réservation annulée",
                "Votre réservation du " + seance.getDateSeance() + " a été annulée."
        );

        return "Réservation annulée avec succès.";
    }

}
