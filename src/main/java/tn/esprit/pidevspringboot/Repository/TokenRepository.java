package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.pidevspringboot.Entities.User.Token;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);

    @Query("SELECT t FROM Token t WHERE t.user.idUser = :id AND t.expired = false AND t.revoked = false")
    List<Token> findAllValidTokensByUser(@Param("id") Long id);
}
