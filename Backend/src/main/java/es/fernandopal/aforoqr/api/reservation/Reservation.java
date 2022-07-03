package es.fernandopal.aforoqr.api.reservation;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.room.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = AppUser.class)
    @JoinColumn(name = "user_uid")
    private AppUser user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Room.class)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Event.class)
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ColumnDefault("false")
    private boolean cancelled;

    @ColumnDefault("true")
    private boolean confirmed;

    private LocalDateTime confirmationDate; // Fecha de confirmación (si hay)
    private LocalDateTime cancellationDate; // Fecha de cancelación (si hay)
    private LocalDateTime requestDate; // Fecha en la que se solicita la reserva

    Reservation(ReservationBuilder builder) {
        this.user = builder.getUser();
        this.room = builder.getRoom();
        this.event = builder.getEvent();
        this.startDate = builder.getStartDate();
        this.endDate = builder.getEndDate();
        this.cancelled = false;
        this.confirmed = false;
        this.confirmationDate = null;
        this.cancellationDate = null;
        this.requestDate = builder.getRequestDate();
    }
}