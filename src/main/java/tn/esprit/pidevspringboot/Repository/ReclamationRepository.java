package tn.esprit.pidevspringboot.Repository;


import tn.esprit.pidevspringboot.Entities.User.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Integer> {
    List<Reclamation> findByArchivedFalse();
}
