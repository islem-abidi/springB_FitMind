package tn.esprit.pidevspringboot.Entities.Menu;

import jakarta.persistence.*;
import lombok.Data;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Date;

@Entity
@Data

public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommande;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "id_plat")
    private Plat plat;

    private Date dateCommande;


    @Enumerated(EnumType.STRING)
    private StatutCommande statut;
}
