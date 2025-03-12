package tn.esprit.pidevspringboot.dto;


import lombok.Getter;
import lombok.Setter;
import tn.esprit.pidevspringboot.Entities.User.Reclamation.TypeReclamation;
import tn.esprit.pidevspringboot.Entities.User.Reclamation.StatutReclamation;
import java.util.Date;

@Getter
@Setter
public class ReclamationRequest {
    private Integer idUser;
    private Date dateReclamation;
    private TypeReclamation typeReclamation;
    private String description;
    private StatutReclamation statut;
    private Date dateResolution;
    private String action;
}

