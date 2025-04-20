package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.pidevspringboot.Entities.Evenement.Evenement;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    @Query("SELECT e FROM Evenement e JOIN e.inscriptions i WHERE i.user.idUser = :idUser AND i.statutInscription = 'CONFIRMEE'")
    List<Evenement> findHistoriqueByUser(@Param("idUser") Long idUser);

    @Query("SELECT e FROM Evenement e WHERE e.idEvenement NOT IN (SELECT i.evenement.idEvenement FROM InscriptionEvenement i WHERE i.user.idUser = :idUser)")
    List<Evenement> findAllNotRegisteredByUser(@Param("idUser") Long idUser);


}