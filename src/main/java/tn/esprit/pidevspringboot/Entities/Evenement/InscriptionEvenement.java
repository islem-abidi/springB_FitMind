package tn.esprit.pidevspringboot.Entities.Evenement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.pidevspringboot.Entities.User.User;


import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inscription_evenement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_evenement"}))

public class InscriptionEvenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInscription;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user; // Relation 1:N avec User

    @ManyToOne
    @JoinColumn(name = "id_evenement")
    @JsonBackReference // ✅ empêche la sérialisation de l'objet "parent"
    private Evenement evenement;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateInscription;

    @Column(nullable = true)
    private String qrCodePath; // Chemin du fichier QR code

    @Column(nullable = true)
    private String ticketPdfPath; // Chemin du fichier PDF du billet

    @Column(nullable = false)
    private boolean qrCodeGenerated = false; // Indique si le QR code a été généré

    // Changé de Boolean (wrapper) à boolean (primitif) pour éviter les NullPointerException
    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    public boolean isReminderSent() {
        return reminderSent != null && reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    // Getters et setters pour les nouveaux champs
    public String getQrCodePath() {
        return qrCodePath;
    }

    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

    public String getTicketPdfPath() {
        return ticketPdfPath;
    }

    public void setTicketPdfPath(String ticketPdfPath) {
        this.ticketPdfPath = ticketPdfPath;
    }

    public boolean isQrCodeGenerated() {
        return qrCodeGenerated;
    }

    public void setQrCodeGenerated(boolean qrCodeGenerated) {
        this.qrCodeGenerated = qrCodeGenerated;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statutInscription; // Relation ENUM 1:1 avec StatutInscription

    public Long getIdInscription() {
        return idInscription;
    }

    public void setIdInscription(Long idInscription) {
        this.idInscription = idInscription;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    public StatutInscription getStatutInscription() {
        return statutInscription;
    }

    public void setStatutInscription(StatutInscription statutInscription) {
        this.statutInscription = statutInscription;
    }
}