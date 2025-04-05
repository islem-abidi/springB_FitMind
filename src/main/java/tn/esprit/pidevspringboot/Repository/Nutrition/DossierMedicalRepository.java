package tn.esprit.pidevspringboot.Repository.Nutrition;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;
import tn.esprit.pidevspringboot.Entities.User.User;
import java.util.List;

import java.util.Optional;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {

    List<DossierMedical> findByArchivedFalse();

    Optional<DossierMedical> findByUser(User user);
}
