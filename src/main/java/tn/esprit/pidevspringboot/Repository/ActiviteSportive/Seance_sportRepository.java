package tn.esprit.pidevspringboot.Repository.ActiviteSportive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Seance_sport;

import java.util.List;

public interface Seance_sportRepository extends JpaRepository<Seance_sport, Long> {

    List<Seance_sport> findByActiviteId(Long id);

}
