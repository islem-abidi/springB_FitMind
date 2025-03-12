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
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateHeure;

    private int duree;
    private String remarque;
    private boolean rappel;

    public Long getIdRendezVous() {
        return idRendezVous;
    }

    public void setIdRendezVous(Long idRendezVous) {
        this.idRendezVous = idRendezVous;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RendezVous that = (RendezVous) o;
        return duree == that.duree && rappel == that.rappel && Objects.equals(idRendezVous, that.idRendezVous) && Objects.equals(user, that.user) && Objects.equals(dateHeure, that.dateHeure) && Objects.equals(remarque, that.remarque);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRendezVous, user, dateHeure, duree, remarque, rappel);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}