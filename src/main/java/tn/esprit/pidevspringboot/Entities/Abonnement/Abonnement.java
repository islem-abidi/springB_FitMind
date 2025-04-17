package tn.esprit.pidevspringboot.Entities.Abonnement;
import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.pidevspringboot.Entities.User.User;
import java.util.Date;
@Entity
@Data


public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbonnement;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Enumerated(EnumType.STRING)
    private TypeAbonnement typeAbonnement;

    @Enumerated(EnumType.STRING)
    private DureeAbonnement dureeAbonnement;

    private Date dateCreation;
    private Date dateFin;

    @Enumerated(EnumType.STRING)
    private StatutAbonnement statut;

    private float montant;
    private String modePaiement;

}
