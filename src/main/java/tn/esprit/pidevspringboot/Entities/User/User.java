package tn.esprit.pidevspringboot.Entities.User;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.pidevspringboot.Entities.ActiviteSportive.Activite;

import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idUser")
@ToString(exclude = {"reclamationsCreees", "reclamationsTraitees", "activite"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private long idUser;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_naissance")
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false)
    private Sexe sexe;

    @Lob
    @Column(name = "photo_profil", columnDefinition = "LONGTEXT")
    private String photoProfil;

    @Column(name = "numero_de_telephone")
    private Integer numeroDeTelephone;

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
    }
}
