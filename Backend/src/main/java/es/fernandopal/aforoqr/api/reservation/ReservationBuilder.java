package es.fernandopal.aforoqr.api.reservation;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.room.Room;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationBuilder {
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private LocalDateTime requestDate;
    private AppUser user;
    private Event event;
    private Room room;

    public static ReservationBuilder reservation() {
        return new ReservationBuilder();
    }

    public ReservationBuilder withStartDate(LocalDateTime date) {
        this.startDate = date;
        return this;
    }

    public ReservationBuilder withEndDate(LocalDateTime date) {
        this.endDate = date;
        return this;
    }

    public ReservationBuilder withRequestDate(LocalDateTime date) {
        this.requestDate = date;
        return this;
    }

    public ReservationBuilder withUser(AppUser user) {
        this.user = user;
        return this;
    }

    public ReservationBuilder withEvent(Event event) {
        this.event = event;
        return this;
    }

    public ReservationBuilder withRoom(Room room) {
        this.room = room;
        return this;
    }

    public Reservation build() {
        return new Reservation(this);
    }
}
