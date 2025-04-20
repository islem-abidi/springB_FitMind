package tn.esprit.pidevspringboot.Entities.Evenement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evenement")
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvenement;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_fin", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Africa/Tunis")
    private LocalDateTime dateFin;

    @Column(name = "date_evenement", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Africa/Tunis")
    private LocalDateTime dateEvenement;


    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }




    @Column(nullable = false, length = 255)
    private String lieu;

    @Column(nullable = true)
    private Integer capaciteMax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement typeEvenement; // Relation ENUM 1:1 avec TypeEvenement

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_event") // très important !
    private EtatEvent etatEvent;


    @Column(name = "image")
    private String image;

    @OneToMany(mappedBy = "evenement", fetch = FetchType.EAGER, cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonManagedReference // ✅ indique le point d’entrée de la relation
    private List<InscriptionEvenement> inscriptions;


    public Long getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(Long idEvenement) {
        this.idEvenement = idEvenement;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public String getLieu() {
        return lieu;
    }

    public EtatEvent getEtatEvent() {
        return etatEvent;
    }

    public void setEtatEvent(EtatEvent etatEvent) {
        this.etatEvent = etatEvent;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public TypeEvenement getTypeEvenement() {
        return typeEvenement;
    }

    public void setTypeEvenement(TypeEvenement typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<InscriptionEvenement> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<InscriptionEvenement> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }
}
