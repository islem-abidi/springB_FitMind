package tn.esprit.pidevspringboot.Repository.User;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pidevspringboot.Entities.User.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
