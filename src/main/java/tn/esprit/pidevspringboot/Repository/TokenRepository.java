package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.User.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
}
