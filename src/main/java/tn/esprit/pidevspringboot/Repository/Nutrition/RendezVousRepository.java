package tn.esprit.pidevspringboot.Repository.Nutrition;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;

import java.util.Optional;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

}
