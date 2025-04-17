package tn.esprit.pidevspringboot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pidevspringboot.Entities.User.Role;
import tn.esprit.pidevspringboot.Entities.User.Roletype;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleType(Roletype roleType);


}
