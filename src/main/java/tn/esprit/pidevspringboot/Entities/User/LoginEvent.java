package tn.esprit.pidevspringboot.Entities.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_user")
    private User user;

    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;
}
