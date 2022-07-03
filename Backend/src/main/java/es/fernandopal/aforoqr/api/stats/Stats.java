package es.fernandopal.aforoqr.api.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Stats {
    private Integer totalUsers;
    private Integer totalReservations;
    private Integer totalEvents;
    private Integer totalRooms;
}