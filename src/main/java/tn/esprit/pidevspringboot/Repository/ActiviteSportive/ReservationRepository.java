package tn.esprit.pidevspringboot.Repository.ActiviteSportive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Reservation;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Status;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findBySeance_IdAndStatusOrderByDateReservationAsc(Long seanceId, Status status);
    Long countByUserIdAndStatus(Long userId, Status status);
    List<Reservation> findByUser_Id(Long userId);

    int countBySeance_Activite_Id(Long activiteId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.seance.activite.id = :activiteId " +
            "AND r.status = 'CONFIRMEE' " +
            "AND WEEK(r.seance.dateSeance) = WEEK(CURRENT_DATE) " +
            "AND YEAR(r.seance.dateSeance) = YEAR(CURRENT_DATE)")
    int countByActiviteIdThisWeek(@Param("activiteId") Long activiteId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE r.seance.activite.id = :activiteId " +
            "AND r.status = 'CONFIRMEE' " +
            "AND FUNCTION('WEEK', r.seance.dateSeance) = FUNCTION('WEEK', CURRENT_DATE) " +
            "AND FUNCTION('YEAR', r.seance.dateSeance) = FUNCTION('YEAR', CURRENT_DATE)")
    int countConfirmedReservationsThisWeek(@Param("activiteId") Long activiteId);

}
