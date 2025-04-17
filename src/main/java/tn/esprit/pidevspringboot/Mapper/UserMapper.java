package tn.esprit.pidevspringboot.Mapper;

import org.springframework.stereotype.Component;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.dto.UserResponse;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getIdUser(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getDateNaissance(),
                user.getSexe(),
                user.getNumeroDeTelephone(),
                user.getPhotoProfil(),
                (user.getRole() != null) ? user.getRole().getRoleType().name() : null,
                user.isArchived()
        );
    }
}
