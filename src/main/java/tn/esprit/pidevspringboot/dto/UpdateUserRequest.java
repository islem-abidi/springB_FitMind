package tn.esprit.pidevspringboot.dto;

import lombok.Getter;
import lombok.Setter;
import tn.esprit.pidevspringboot.Entities.User.Sexe;

import java.util.Date;

@Getter
@Setter
public class UpdateUserRequest {
    private String nom;
    private String prenom;
    private Long numeroDeTelephone;
    private String photoProfil;
    private String email;
    private Date dateNaissance;
    private Sexe sexe;

}
