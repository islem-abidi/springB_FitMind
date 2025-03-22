package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidevspringboot.Entities.User.Token;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);

}
