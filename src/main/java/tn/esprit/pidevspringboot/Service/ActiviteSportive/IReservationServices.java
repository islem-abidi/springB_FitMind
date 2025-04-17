package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Reservation;

import java.util.List;

public interface IReservationServices {
    List<Reservation> getReservationsByUser(Long userId);

    Reservation ajouterAListAttente(Long idSeance, Long idUser);
    List<Reservation> getListeAttente(Long seanceId);
    void confirmerProchaineReservation(Long seanceId);
    String confirmerReservationParUtilisateur(Long reservationId);

    String annulerReservation(Long reservationId);
}
