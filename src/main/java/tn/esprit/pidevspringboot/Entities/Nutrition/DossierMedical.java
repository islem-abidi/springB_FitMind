package tn.esprit.pidevspringboot.Entities.Nutrition;

import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.pidevspringboot.Entities.User.User;

@Entity
@Data
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDossier;

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    private String maladies;
    private String allergies;
    private String objectifSante;
    private String traitements;
    private float imc;
    private float tailles;
    private float poids;

    @Enumerated(EnumType.STRING)
    private GroupSanguin groupeSanguin;

    public Long getIdDossier() {
        return idDossier;
    }

    public void setIdDossier(Long idDossier) {
        this.idDossier = idDossier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMaladies() {
        return maladies;
    }

    public void setMaladies(String maladies) {
        this.maladies = maladies;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getObjectifSante() {
        return objectifSante;
    }

    public void setObjectifSante(String objectifSante) {
        this.objectifSante = objectifSante;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
    }

    public String getTraitements() {
        return traitements;
    }

    public void setTraitements(String traitements) {
        this.traitements = traitements;
    }

    public float getTailles() {
        return tailles;
    }

    public void setTailles(float tailles) {
        this.tailles = tailles;
    }

    public float getPoids() {
        return poids;
    }

    public void setPoids(float poids) {
        this.poids = poids;
    }

    public GroupSanguin getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(GroupSanguin groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }
}