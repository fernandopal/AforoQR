package es.fernandopal.aforoqr.api.room;

import es.fernandopal.aforoqr.api.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/room")
public class RoomController {

    private final RoomService roomService;
    private final SecurityService securityService;

    // fpalomo - Returns a list of all rooms
    @GetMapping()
    @Operation(description = "Returns a list of all the stored Room records in the database")
    public ResponseEntity<List<Room>> getAll() {
        List<Room> rooms = roomService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(rooms);
    }

    // fpalomo - Get a room by an id
    @GetMapping(value = "/{id}")
    @Operation(description = "Returns all the data stored in the database about a Room")
    public ResponseEntity<Room> getById(@PathVariable("id") Long id) {
        Room room = roomService.getRoom(id).orElse(null);
        HttpStatus responseStatus = (room == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(responseStatus).body(room);
    }

    // fpalomo - Inserts a room on the database, returns the Id of the saved room
    @PostMapping
    @Operation(description = "Persist a Room record")
    public ResponseEntity<Long> saveRoom(@RequestBody Room room) {
        roomService.save(room, securityService.getUser());
        return ResponseEntity.ok().body(room.getId());
    }

    // fpalomo - Delete a room
    @DeleteMapping("/{id}")
    @Operation(description = "Delete a Room record")
    public ResponseEntity<String> deleteRoom(@PathVariable("id") Long id) {
        roomService.delete(id);
        return ResponseEntity.ok().body("Ok");
    }
}
