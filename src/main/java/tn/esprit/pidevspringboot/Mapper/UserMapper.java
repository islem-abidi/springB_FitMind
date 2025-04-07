package tn.esprit.pidevspringboot.Mapper;

import org.springframework.stereotype.Component;
import tn.esprit.pidevspringboot.Entities.User.User;
import tn.esprit.pidevspringboot.dto.UserResponse;

@Component
public class UserMapper {

    public UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();

        response.setIdUser(user.getIdUser());
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setEmail(user.getEmail());
        response.setDateNaissance(user.getDateNaissance());
        response.setSexe(user.getSexe());
        response.setNumeroDeTelephone(user.getNumeroDeTelephone());
        response.setPhotoProfil(user.getPhotoProfil());
        response.setRole(user.getRole().getRoleType().toString());

        return response;
    }
}
