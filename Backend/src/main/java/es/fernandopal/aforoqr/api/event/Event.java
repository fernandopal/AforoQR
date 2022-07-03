package es.fernandopal.aforoqr.api.event;

import es.fernandopal.aforoqr.api.room.Room;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity = Room.class)
    @JoinColumn(name = "room_id")
    private Room room;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ColumnDefault("true")
    private boolean active;
}