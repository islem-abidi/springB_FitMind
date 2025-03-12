package tn.esprit.pidevspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.pidevspringboot.Entities.User.Sexe;

import java.util.Date;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private Date dateNaissance;
    private Sexe sexe;
    private Integer numeroDeTelephone;
    private String photoProfil;
    private Integer id_role;

}
