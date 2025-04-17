package tn.esprit.pidevspringboot.Entities.User;

import jakarta.persistence.*;
import lombok.*;

<<<<<<< HEAD

=======
>>>>>>> origin/gestionNutrition
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
<<<<<<< HEAD
@EqualsAndHashCode(of = "id_role")
@ToString
=======
>>>>>>> origin/gestionNutrition
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
<<<<<<< HEAD
    private Integer id_role;
=======
    private Integer id;
>>>>>>> origin/gestionNutrition

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, unique = true)
    private Roletype roleType;

<<<<<<< HEAD
    public Integer getId_role() {
        return id_role;
    }

    public void setId_role(Integer id_role) {
        this.id_role = id_role;
    }

    public Roletype getRoleType() {
        return roleType;
    }

    public void setRoleType(Roletype roleType) {
        this.roleType = roleType;
    }
=======
>>>>>>> origin/gestionNutrition

}
