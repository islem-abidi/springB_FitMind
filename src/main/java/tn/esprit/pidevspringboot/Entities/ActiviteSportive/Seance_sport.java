package tn.esprit.pidevspringboot.Entities.ActiviteSportive;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Seance_sport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seance")
     Long id;


    @NotNull(message = "L'heure de début est obligatoire.")
    @Temporal(TemporalType.TIME)
    @JsonFormat(pattern = "HH:mm")

    String heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire.")
    @Temporal(TemporalType.TIME)
    @JsonFormat(pattern = "HH:mm")

    String heureFin;

    @Min(value = 1, message = "La capacité maximale doit être au moins de 1.")
    int capaciteMax;
    @Min(value = 0, message = "La capacité disponible ne peut pas être négative.")
    int capaciteDispo;

    @NotBlank(message = "Le lieu est obligatoire.")
    @Size(min = 2, max = 100, message = "Le lieu doit contenir entre 2 et 100 caractères.")
     String lieu;

    @NotNull(message = "La date de la séance est obligatoire.")
    @Temporal(TemporalType.DATE)
    LocalDate dateSeance;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "activite_id_activite") // 👈 doit correspondre au champ en base
    Activite activite;

    @OneToMany(mappedBy = "seance")
    @JsonIgnore
    Set<Reservation> reservations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public Activite getActivite() {
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public int getCapaciteDispo() {
        return capaciteDispo;
    }

    public void setCapaciteDispo(int capaciteDispo) {
        this.capaciteDispo = capaciteDispo;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }


}
