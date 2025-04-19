package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.Evenement.Evenement;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

}