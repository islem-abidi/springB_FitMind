package tn.esprit.pidevspringboot.Repository.ActiviteSportive;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
