package tn.esprit.pidevspringboot.Repository.ActiviteSportive;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Reservation;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Status;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findBySeance_IdAndStatusOrderByDateReservationAsc(Long seanceId, Status status);
    Long countByUserIdAndStatus(Long userId, Status status);

}
