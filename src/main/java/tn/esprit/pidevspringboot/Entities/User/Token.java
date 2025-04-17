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
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private boolean expired;
    @Column(nullable = false)
    private boolean isValid;

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }


}


