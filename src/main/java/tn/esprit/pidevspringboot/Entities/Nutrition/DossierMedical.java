package tn.esprit.pidevspringboot.Entities.Nutrition;

import jakarta.persistence.*;
<<<<<<< HEAD
=======
import jakarta.validation.constraints.*;
>>>>>>> origin/gestionNutrition
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

<<<<<<< HEAD
    private String maladies;
    private String allergies;
    private String objectifSante;
    private String traitements;
    private float imc;
    private float tailles;
    private float poids;

    @Enumerated(EnumType.STRING)
    private GroupSanguin groupeSanguin;
}
=======
    @NotBlank(message = "Le champ maladies est obligatoire.")
    @Size(max = 255, message = "Le champ maladies ne doit pas dépasser 255 caractères.")
    private String maladies;

    @NotBlank(message = "Le champ objectif santé est obligatoire.")
    @Size(max = 255, message = "Le champ objectif santé ne doit pas dépasser 255 caractères.")
    private String objectifSante;

    @NotBlank(message = "Le champ traitements est obligatoire.")
    private String traitements;

    @Max(value = 250, message = "La taille ne doit pas dépasser 250 cm.")
    private float tailles;

    @Max(value = 300, message = "Le poids ne doit pas dépasser 300 kg.")
    private float poids;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le groupe sanguin est obligatoire.")
    private GroupSanguin groupeSanguin;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "L'Allergie est obligatoire.")
    private Allergies Allergies;

    private Boolean archived = false;

    // ✅ Nouveau champ ajouté ici
    private Boolean rdvRecommande = false;

    private float imc;

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
    }

    // ======= Getters/Setters =======
    public Allergies getAllergies() {
        return Allergies;
    }

    public void setAllergies(Allergies allergies) {
        Allergies = allergies;
    }

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

    public String getObjectifSante() {
        return objectifSante;
    }

    public void setObjectifSante(String objectifSante) {
        this.objectifSante = objectifSante;
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

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public boolean isArchived() {
        return archived;
    }

    // ✅ Getter et Setter pour rdvRecommande
    public Boolean getRdvRecommande() {
        return rdvRecommande;
    }

    public void setRdvRecommande(Boolean rdvRecommande) {
        this.rdvRecommande = rdvRecommande;
    }
}
>>>>>>> origin/gestionNutrition
