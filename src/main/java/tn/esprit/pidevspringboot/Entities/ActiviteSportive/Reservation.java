package tn.esprit.pidevspringboot.Entities.ActiviteSportive;

<<<<<<< HEAD
=======
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
>>>>>>> origin/GestionActivite-Sportive
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.CrossOrigin;
import tn.esprit.pidevspringboot.Entities.User.User;
>>>>>>> origin/GestionActivite-Sportive

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
<<<<<<< HEAD
=======
@CrossOrigin(origins = "http://localhost:4200")
>>>>>>> origin/GestionActivite-Sportive

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Long id_reservation;
=======
     long id_reservation;
>>>>>>> origin/GestionActivite-Sportive

    @Enumerated (EnumType.STRING)
    Status status;

<<<<<<< HEAD
    @Temporal(TemporalType.DATE)
    private Date dateReservation;

    @ManyToOne
    Seance_sport seance_sport;
=======
     Date dateReservation;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    @JsonIgnoreProperties({"reservations"})
// éviter boucle infinie
    Seance_sport seance;

    @ManyToOne
    @JoinColumn(name = "id_user")
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(long id_reservation) {
        this.id_reservation = id_reservation;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public Seance_sport getSeance() {
        return seance;
    }

    public void setSeance(Seance_sport seance) {
        this.seance = seance;
    }
>>>>>>> origin/GestionActivite-Sportive
}

