package es.fernandopal.aforoqr.api.stats;

import es.fernandopal.aforoqr.api.appuser.AppUserService;
import es.fernandopal.aforoqr.api.event.EventService;
import es.fernandopal.aforoqr.api.reservation.ReservationService;
import es.fernandopal.aforoqr.api.room.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/stats")
public class StatsController {
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ReservationService reservationService;

    // fpalomo - Returns all the app stats
    @GetMapping()
    @Operation(description = "Get general stats of the service")
    public ResponseEntity<Stats> get() {
        final Stats stats = new Stats(
                appUserService.getUserCount(),
                reservationService.getReservationCount(),
                eventService.getEventCount(),
                roomService.getRoomCount()
        );

        return ResponseEntity.status(HttpStatus.OK).body(stats);
    }
}
