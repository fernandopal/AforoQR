package es.fernandopal.aforoqr.api.qrcode;

import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.room.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class QRCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String data;
    private String imagePath;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Room.class)
    @JoinColumn(name = "room_id", unique = true)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, targetEntity = Event.class)
    @JoinColumn(name = "event_id", unique = true)
    private Event event;

    public QRCode(String data, Room room, Event event) {
        this.data = data;
        this.room = room;
        this.event = event;
    }

    public QRCode(String data) {
        this.data = data;
    }
}
