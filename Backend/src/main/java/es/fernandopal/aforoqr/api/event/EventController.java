package es.fernandopal.aforoqr.api.event;

import es.fernandopal.aforoqr.api.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/event")
public class EventController {

    private final EventService eventService;
    private final SecurityService securityService;

    // fpalomo - Returns a list of all events
    @GetMapping()
    @Operation(description = "Returns a list of all the stored Event records in the database")
    public ResponseEntity<List<Event>> getAll() {
        List<Event> events = eventService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    // fpalomo - Get an event by its id
    @GetMapping(value = "/{id}")
    @Operation(description = "Returns all the data stored in the database about an Event")
    public ResponseEntity<Event> getById(@PathVariable("id") Long id) {
        Event event = eventService.getEvent(id).orElse(null);
        HttpStatus responseStatus = (event == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(responseStatus).body(event);
    }

    // fpalomo - Inserts an event on the database, returns the Id of the saved event
    @PostMapping
    @Operation(description = "Persist an Event record")
    public ResponseEntity<Long> saveAddress(@RequestBody Event event) {
        eventService.save(event, securityService.getUser());
        return ResponseEntity.ok().body(event.getId());
    }
}