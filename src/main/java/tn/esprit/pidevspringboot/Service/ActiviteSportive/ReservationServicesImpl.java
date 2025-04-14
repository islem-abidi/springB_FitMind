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

        String subject = "Confirmez votre réservation 🕓";
        String body = "Bonjour " + user.getNom() + ",\n\n" +
                "Vous êtes en liste d'attente pour l'activité '" + seance.getActivite().getNomActivite() + "'.\n" +
                "Date : " + seance.getDateSeance() + "\n" +
                "Lieu : " + seance.getLieu() + "\n\n" +
                "👉 Pour confirmer votre participation (si une place est disponible), cliquez ici :\n" +
                confirmationLink + "\n\n" +
                "Merci et à très bientôt !";

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
        emailService.sendConfirmationEmail(
                reservation.getUser().getEmail(),
                "Réservation Confirmée ✅",
                "Bonjour " + reservation.getUser().getNom() + ",\n\n" +
                        "Votre réservation pour la séance '" + seance.getActivite().getNomActivite() + "' est désormais confirmée.\n" +
                        "À bientôt !"
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
}
