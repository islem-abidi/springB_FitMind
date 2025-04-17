package tn.esprit.pidevspringboot.Controller.ActiviteSportive;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Reservation;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ReservationRepository;
import tn.esprit.pidevspringboot.Service.ActiviteSportive.IReservationServices;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/reservations")
@Tag(name= "gestion des réservations")
@CrossOrigin(origins = "http://localhost:4200")

public class ReservationRestController {
    @Autowired
    IReservationServices reservationServices;
    @Autowired
    ReservationRepository reservationRepository;
    @GetMapping("/confirm-reservation/{reservationId}") // <-- AJOUT DU GET
    public String confirmerReservationGet(@PathVariable Long reservationId) {
        return reservationServices.confirmerReservationParUtilisateur(reservationId);
    }

    @PutMapping("/confirm-reservation/{reservationId}")
    public String confirmerReservation(@PathVariable Long reservationId) {
        System.out.println("🔵 Requête reçue pour confirmation ID : " + reservationId);
        return reservationServices.confirmerReservationParUtilisateur(reservationId);
    }



    @PostMapping("/attente")
    public Reservation ajouterAListAttente(@RequestParam Long seanceId, @RequestParam Long userId) {
        return reservationServices.ajouterAListAttente(seanceId, userId);
    }


    @PutMapping("/confirmation/{seanceId}")
    public void confirmerPlace(@PathVariable Long seanceId) {
        reservationServices.confirmerProchaineReservation(seanceId);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getReservationsByUser(@PathVariable Long userId) {
        return reservationRepository.findByUser_Id(userId); // CRASH ici
    }

    @DeleteMapping("/annuler/{reservationId}")
    public String annulerReservation(@PathVariable Long reservationId) {
        return reservationServices.annulerReservation(reservationId);
    }

}
