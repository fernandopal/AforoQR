package es.fernandopal.aforoqr.api.reservation.token;

import es.fernandopal.aforoqr.api.reservation.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Reservation.class)
    @JoinColumn(nullable = false, name = "reservation_id")
    private Reservation reservation;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Reservation reservation) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.reservation = reservation;
    }
}
