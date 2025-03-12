package tn.esprit.pidevspringboot.Repository.Nutrition;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.Nutrition.DossierMedical;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
}
