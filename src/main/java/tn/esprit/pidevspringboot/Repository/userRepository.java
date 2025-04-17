package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.User.User;

public interface userRepository extends JpaRepository<User, Long> {
}
