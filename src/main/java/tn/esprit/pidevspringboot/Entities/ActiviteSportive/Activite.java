package tn.esprit.pidevspringboot.Entities.ActiviteSportive;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.pidevspringboot.Entities.User.User;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties

public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_activite") // ← CORRESPOND AU NOM EXACT EN BASE
    long id;

    @Column(nullable = false)
    @NotBlank(message = "Le nom de l'activité ne peut pas être vide.")
    @Size(min = 3, max = 50, message = "Le nom doit comporter entre 3 et 50 caractères.")

    String nomActivite;
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères.")

     String description;
    String imageUrl;

    @Enumerated(EnumType.STRING)
    StatutActivite statutActivite;
    @ManyToMany(mappedBy = "activite", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("activite")
    Set<User>user;
    @OneToMany(mappedBy = "activite")
    Set<Seance_sport> seance_sports;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomActivite() {
        return nomActivite;
    }

    public void setNomActivite(String nomActivite) {
        this.nomActivite = nomActivite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUser() {
        return user;
    }

    public void setUser(Set<User> user) {
        this.user = user;
    }

    public Set<Seance_sport> getSeance_sports() {
        return seance_sports;
    }

    public void setSeance_sports(Set<Seance_sport> seance_sports) {
        this.seance_sports = seance_sports;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public StatutActivite getStatutActivite() {
        return statutActivite;
    }

    public void setStatutActivite(StatutActivite statutActivite) {
        this.statutActivite = statutActivite;
    }


}
