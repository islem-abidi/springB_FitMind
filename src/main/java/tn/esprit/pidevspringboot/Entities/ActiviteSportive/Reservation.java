package tn.esprit.pidevspringboot.Entities.ActiviteSportive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id_reservation;

    @Enumerated (EnumType.STRING)
    Status status;

     Date dateReservation;

    @ManyToOne
    @JoinColumn(name = "id_seance")
    @JsonIgnore
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
}

