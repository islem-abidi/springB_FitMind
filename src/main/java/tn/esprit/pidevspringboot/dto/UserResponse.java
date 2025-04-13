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
    private boolean archived;

    // ✅ Constructeur utilisé pour le mapping
    public UserResponse(Long idUser, String nom, String prenom, String email,
                        Date dateNaissance, Sexe sexe, Integer numeroDeTelephone,
                        String photoProfil, String role, boolean archived) {
        this.idUser = idUser;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.sexe = sexe;
        this.numeroDeTelephone = numeroDeTelephone;
        this.photoProfil = photoProfil;
        this.role = role;
        this.archived = archived;
    }
}
