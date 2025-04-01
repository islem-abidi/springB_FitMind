package tn.esprit.pidevspringboot.Entities.Nutrition;

import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Date;
import java.util.Objects;

@Entity
@Data
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRendezVous;

    @ManyToOne
    @JoinColumn(name = "nutritioniste_id", nullable = false)
    private User nutritioniste;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;


    @Temporal(TemporalType.TIMESTAMP)
    private Date dateHeure;

    private int duree;
    private String remarque;

    public Long getIdRendezVous() {
        return idRendezVous;
    }

    public void setIdRendezVous(Long idRendezVous) {
        this.idRendezVous = idRendezVous;
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


    public User getUser() {
        return null;
    }

    public void setUser(User user) {
    }
}