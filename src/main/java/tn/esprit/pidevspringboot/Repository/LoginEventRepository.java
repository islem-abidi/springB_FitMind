package tn.esprit.pidevspringboot.Repository;

import tn.esprit.pidevspringboot.Entities.User.LoginEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginEventRepository extends JpaRepository<LoginEvent, Long> {
    List<LoginEvent> findByLoginDateAfter(LocalDateTime date);
    Long countByUser_IdUser(Long idUser);
}
