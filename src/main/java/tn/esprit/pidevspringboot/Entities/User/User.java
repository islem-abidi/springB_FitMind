package tn.esprit.pidevspringboot.Entities.User;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.*;
<<<<<<< HEAD
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
=======
import org.hibernate.annotations.GenericGenerator;
import tn.esprit.pidevspringboot.Entities.Abonnement.Abonnement;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
import tn.esprit.pidevspringboot.Entities.Nutrition.RendezVous;
>>>>>>> origin/gestionNutrition
=======
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import tn.esprit.pidevspringboot.Entities.Abonnement.Abonnement;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;
>>>>>>> origin/GestionActivite-Sportive

import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
<<<<<<< HEAD
@EqualsAndHashCode(of = "idUser")
@ToString(exclude = {"reclamationsCreees", "reclamationsTraitees", "activite"})
public class User implements UserDetails {
=======
public class User {
>>>>>>> origin/gestionNutrition
=======
public class User {
>>>>>>> origin/GestionActivite-Sportive

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
<<<<<<< HEAD
<<<<<<< HEAD
    private long idUser;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
=======
    private Long idUser;


    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
>>>>>>> origin/gestionNutrition
    private String motDePasse;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_naissance")
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private Sexe sexe;

<<<<<<< HEAD
    @Lob
    @Column(name = "photo_profil", columnDefinition = "LONGTEXT")
=======
=======
    long id;


    @Column(name = "nom", length = 100, nullable = false)
    String nom;

    @Column(name = "prenom", length = 100, nullable = false)
    String prenom;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
    String motDePasse;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_naissance")
    Date dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    Sexe sexe;

>>>>>>> origin/GestionActivite-Sportive
    public enum Sexe {
        Homme, Femme, Autre
    }

    @Column(name = "photo_profil", length = 255)
<<<<<<< HEAD
>>>>>>> origin/gestionNutrition
=======
>>>>>>> origin/GestionActivite-Sportive
    private String photoProfil;

    @Column(name = "numero_de_telephone")
    private Integer numeroDeTelephone;

<<<<<<< HEAD
<<<<<<< HEAD
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_role")
    private Role role;
    @Column(name = "banned", nullable = false)
    private boolean banned = false;

    @ManyToMany
    private Set<Activite> activite = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Token> tokens = new ArrayList<>();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    private List<Reclamation> reclamationsCreees;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    private List<Reclamation> reclamationsTraitees;

    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "reset_token")
    private String resetToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reset_token_time")
    private Date resetTokenTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(() -> "ROLE_" + this.role.getRoleType().name());
    }

    @Override
    public String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
=======
=======
>>>>>>> origin/GestionActivite-Sportive
    @ManyToOne
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reclamation> reclamations = new ArrayList<>();

    @ManyToMany
<<<<<<< HEAD
=======
    @JsonIgnoreProperties("user")
    @JsonIgnore
>>>>>>> origin/GestionActivite-Sportive
    Set<Activite> activite;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Abonnement> abonnements = new ArrayList<>();

<<<<<<< HEAD
    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
=======
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
>>>>>>> origin/GestionActivite-Sportive
    }

    public String getNom() {
        return nom;
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public String getPhotoProfil() {
        return photoProfil;
    }

    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }

    public Integer getNumeroDeTelephone() {
        return numeroDeTelephone;
    }

    public void setNumeroDeTelephone(Integer numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Reclamation> getReclamations() {
        return reclamations;
    }

    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }

    public Set<Activite> getActivite() {
        return activite;
    }

    public void setActivite(Set<Activite> activite) {
        this.activite = activite;
    }

    public List<Abonnement> getAbonnements() {
        return abonnements;
    }

    public void setAbonnements(List<Abonnement> abonnements) {
        this.abonnements = abonnements;
<<<<<<< HEAD
>>>>>>> origin/gestionNutrition
=======
>>>>>>> origin/GestionActivite-Sportive
    }
}
