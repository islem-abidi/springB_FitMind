package tn.esprit.pidevspringboot.dto;


import lombok.*;
import tn.esprit.pidevspringboot.Entities.User.Sexe;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private long idUser;
    private String nom;
    private String prenom;
    private String email;
    private Date dateNaissance;
    private Sexe sexe;
    private Integer numeroDeTelephone;
    private String photoProfil;
    private String role;
}

