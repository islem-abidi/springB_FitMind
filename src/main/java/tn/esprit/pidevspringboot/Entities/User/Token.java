package tn.esprit.pidevspringboot.Entities.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean isValid; // bch na3rf el token actif wale

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
