package tn.esprit.pidevspringboot.Entities.Nutrition;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Date;

@Entity
@Data
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRendezVous;

    @ManyToOne
    @JoinColumn(name = "nutritioniste_id", nullable = true)
    private User nutritioniste;

    private boolean rappel;
    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateHeure;

    @Min(value = 30, message = "La durée minimale d'un rendez-vous est de 30 minutes.")
    @Max(value = 120, message = "La durée maximale d'un rendez-vous est de 120 minutes.")
    private int duree;

    @NotBlank(message = "Le champ remarque est obligatoire.")
    @Size(max = 500, message = "La remarque ne doit pas dépasser 500 caractères.")
    private String remarque;

    private boolean archived = false;

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

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

}
