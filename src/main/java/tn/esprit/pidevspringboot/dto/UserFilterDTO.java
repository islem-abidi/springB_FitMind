package tn.esprit.pidevspringboot.dto;

import tn.esprit.pidevspringboot.Entities.User.Sexe;

import java.util.Date;

public class UserFilterDTO {
    private String nom;
    private String prenom;
    private String email;
    private Sexe sexe;
    private Date dateNaissanceMin;
    private Date dateNaissanceMax;
    private Boolean archived;
    private Long idRole;

    public String getNom() {
        return nom;
    }

    public Long getIdRole() {
        return idRole;
    }

    public void setIdRole(Long idRole) {
        this.idRole = idRole;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public Date getDateNaissanceMin() {
        return dateNaissanceMin;
    }

    public void setDateNaissanceMin(Date dateNaissanceMin) {
        this.dateNaissanceMin = dateNaissanceMin;
    }

    public Date getDateNaissanceMax() {
        return dateNaissanceMax;
    }

    public void setDateNaissanceMax(Date dateNaissanceMax) {
        this.dateNaissanceMax = dateNaissanceMax;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }
}

