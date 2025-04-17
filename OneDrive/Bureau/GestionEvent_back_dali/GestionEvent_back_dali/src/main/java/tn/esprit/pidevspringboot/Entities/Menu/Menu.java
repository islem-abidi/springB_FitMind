package tn.esprit.pidevspringboot.Entities.Menu;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMenu;
    private Date dateMenu;
    private String typeRepas;

    @OneToMany(mappedBy = "menu")
    private List<Plat> plats;

}
