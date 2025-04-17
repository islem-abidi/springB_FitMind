package tn.esprit.pidevspringboot.Entities.Menu;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlat;
    private String nomPlat;
    private String categorie;
    private int calories;
    private String allergenes;

    @ManyToOne
    @JoinColumn(name = "id_menu", nullable = false)
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private StatutPlat statut;
}
