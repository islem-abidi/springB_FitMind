package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.pidevspringboot.Entities.Evenement.InscriptionEvenement;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionEvenementRepository extends JpaRepository<InscriptionEvenement, Long> {

    @Query("SELECT i FROM InscriptionEvenement i WHERE i.user.idUser = :idUser AND i.evenement.idEvenement = :idEvenement")
    Optional<InscriptionEvenement> findByUserIdAndEventId(@Param("idUser") Long idUser, @Param("idEvenement") Long idEvenement);
    // Dans InscriptionEvenementRepository.java
    List<InscriptionEvenement> findByEvenementIdEvenement(Long idEvenement);
    @Query("SELECT i.idInscription FROM InscriptionEvenement i WHERE i.user.idUser = :idUser AND i.evenement.idEvenement = :idEvenement")
    Long findInscriptionIdByUserAndEvent(@Param("idUser") Long idUser, @Param("idEvenement") Long idEvenement);
}
