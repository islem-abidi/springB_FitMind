package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidevspringboot.Entities.User.Sexe;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIdUserAndArchivedFalse(Long idUser);
    List<User> findAllByArchivedFalse();
    List<User> findByNomContainingIgnoreCase(String nom);
    List<User> findByPrenomContainingIgnoreCase(String prenom);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findBySexe(Sexe sexe);
    List<User> findByArchived(Boolean archived);
    boolean existsByEmail(String email);
    List<User> findAllByArchivedFalseAndIsVerifiedTrue();
    Optional<User> findByResetToken(String token);

}
