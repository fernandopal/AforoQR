package es.fernandopal.aforoqr.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/reservation")
public class ReservationController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    @GetMapping
    @Operation(description = "Returns a list of all the stored Reservation records in the database")
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(description = "Returns all the data stored in the database about an Reservation")
    public ResponseEntity<Reservation> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getById(id).orElse(null));
    }

    @GetMapping(value = "/u/{uid}")
    @Operation(description = "Returns all reservations that are related to the specified user")
    public ResponseEntity<List<Reservation>> getForUser(@PathVariable("uid") String uid) {
        return ResponseEntity.ok().body(reservationService.getReservationsForUser(uid));
    }

    @PostMapping("{id}/send-confirmation-email")
    @Operation(description = "Sends the confirmation email to the user associated with the queried Reservation")
    public ResponseEntity<HttpStatus> sendConfirmationEmail(@PathVariable("id") Long id) {
        reservationService.sendConfirmationEmail(id, null);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("{id}/confirm/{token}")
    @Operation(description = "Validates the confirmation token sent and confirms the Reservation if it is valid ")
    public ResponseEntity<String> confirmReservation(@PathVariable("id") Long id, @PathVariable("token") String token) {
        reservationService.confirmReservation(id, token);
        return ResponseEntity.ok("Reserva confirmada");
    }

    @PostMapping("{id}/cancel")
    @Operation(description = "Cancel the specified reservation")
    public ResponseEntity<HttpStatus> cancelReservation(@PathVariable("id") Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("{id}/delete")
    @Operation(description = "Delete the specified reservation")
    public ResponseEntity<HttpStatus> deleteReservation(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/complex")
    @Operation(description = "Generate a complex query over the Reservations table")
    public ResponseEntity<List<Reservation>> getReservationComplex(@RequestBody ReservationRequest reservationRequest) {
        LOGGER.info(reservationRequest.toString());
        return ResponseEntity.ok().body(reservationService.getReservations(reservationRequest));
    }

    @PostMapping
    @Operation(description = "Persist a Reservation record")
    public ResponseEntity<Long> createReservation(@RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok().body(reservationService.createReservation(reservationRequest).getId());
    }
}