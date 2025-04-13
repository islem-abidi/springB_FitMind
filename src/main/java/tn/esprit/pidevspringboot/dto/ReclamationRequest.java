package tn.esprit.pidevspringboot.dto;


import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import tn.esprit.pidevspringboot.Entities.User.Reclamation.TypeReclamation;
import tn.esprit.pidevspringboot.Entities.User.Reclamation.StatutReclamation;
import java.util.Date;

@Getter
@Setter
public class ReclamationRequest {
    private Integer idUser;
    @Nullable
    private Date dateReclamation;
    private TypeReclamation typeReclamation;
    private String description;
    @Nullable

    private StatutReclamation statut;
    @Nullable

    private Date dateResolution;

    public Long getIdUser() {
        return Long.valueOf(idUser);
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public Date getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(Date dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    public TypeReclamation getTypeReclamation() {
        return typeReclamation;
    }

    public void setTypeReclamation(TypeReclamation typeReclamation) {
        this.typeReclamation = typeReclamation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutReclamation getStatut() {
        return statut;
    }

    public void setStatut(StatutReclamation statut) {
        this.statut = statut;
    }

    public Date getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(Date dateResolution) {
        this.dateResolution = dateResolution;
    }


}

