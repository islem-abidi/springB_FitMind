package tn.esprit.pidevspringboot.Repository.ActiviteSportive;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;

import java.util.List;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {
}