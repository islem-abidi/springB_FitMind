package tn.esprit.pidevspringboot.Entities.Nutrition;

import jakarta.persistence.*;
<<<<<<< HEAD
<<<<<<< HEAD
import lombok.Data;
=======
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
>>>>>>> origin/gestionNutrition
=======
import lombok.Data;
>>>>>>> origin/GestionActivite-Sportive
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Date;

@Entity
<<<<<<< HEAD
<<<<<<< HEAD
@Data
=======
@Getter
@Setter
@ToString
>>>>>>> origin/gestionNutrition
=======
@Data
>>>>>>> origin/GestionActivite-Sportive
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRendezVous;

    @ManyToOne
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> origin/GestionActivite-Sportive
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateHeure;

    private int duree;
    private String remarque;
    private boolean rappelEnvoye;
<<<<<<< HEAD
}
=======
    @JoinColumn(name = "nutritioniste_id")
    private User nutritioniste;

    @ManyToOne(optional = false)
    @JoinColumn(name = "etudiant_id")
    private User etudiant;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "La date et l'heure sont obligatoires.")
    private Date dateHeure;

    @Min(value = 30, message = "La durée minimale est de 30 minutes.")
    @Max(value = 120, message = "La durée maximale est de 120 minutes.")
    private int duree;

    @NotBlank(message = "Le champ remarque est obligatoire.")
    @Size(max = 500, message = "La remarque ne doit pas dépasser 500 caractères.")
    private String remarque;

    private boolean rappel = false;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut = StatutRendezVous.EN_COURS;

    private boolean archived = false;

    public Long getIdRendezVous() {
        return idRendezVous;
    }

    public void setIdRendezVous(Long idRendezVous) {
        this.idRendezVous = idRendezVous;
    }

    public User getNutritioniste() {
        return nutritioniste;
    }

    public void setNutritioniste(User nutritioniste) {
        this.nutritioniste = nutritioniste;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Date getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Date dateHeure) {
        this.dateHeure = dateHeure;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public boolean isRappel() {
        return rappel;
    }

    public void setRappel(boolean rappel) {
        this.rappel = rappel;
    }

    public StatutRendezVous getStatut() {
        return statut;
    }

    public void setStatut(StatutRendezVous statut) {
        this.statut = statut;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
>>>>>>> origin/gestionNutrition
=======
}
>>>>>>> origin/GestionActivite-Sportive
