package tn.esprit.pidevspringboot.Service.ActiviteSportive;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Status;
import tn.esprit.pidevspringboot.Repository.ActiviteSportive.ReservationRepository;

@Service
@AllArgsConstructor
public class BadgeService {

    @Autowired
    ReservationRepository reservationRepository;

    public String getBadgeForUser(Long userId) {
        Long totalConfirmées = reservationRepository.countByUserIdAndStatus(userId, Status.CONFIRMEE);

        if (totalConfirmées >= 50) {
            return "🏅 Machine de guerre";
        } else if (totalConfirmées >= 20) {
            return "Assidu";
        } else if (totalConfirmées >= 5) {
            return "Habitué";
        } else {
            return "Aucun badge";
        }
    }
}

