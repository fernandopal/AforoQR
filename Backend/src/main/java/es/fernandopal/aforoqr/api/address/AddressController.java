package es.fernandopal.aforoqr.api.address;

import es.fernandopal.aforoqr.api.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/address")
public class AddressController {

    private final AddressService addressService;
    private final SecurityService securityService;

    // fpalomo - Returns a list of all rooms
    @GetMapping()
    @Operation(description = "Returns a list of all the stored Address records in the database")
    public ResponseEntity<List<Address>> getAll() {
        List<Address> addresses = addressService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(addresses);
    }

    // fpalomo - Get an address by its id
    @GetMapping(value = "/{id}")
    @Operation(description = "Returns all the data stored in the database about an Address")
    public ResponseEntity<Address> getById(@PathVariable("id") Long id) {
        Address address = addressService.getRoom(id).orElse(null);
        HttpStatus responseStatus = (address == null) ? HttpStatus.NOT_FOUND : HttpStatus.OK;

        return ResponseEntity.status(responseStatus).body(address);
    }

    // fpalomo - Inserts an address on the database, returns the Id of the saved address
    @PostMapping
    @Operation(description = "Persist an Address record")
    public ResponseEntity<Long> saveAddress(@RequestBody Address address) {
        addressService.save(address, securityService.getUser());
        return ResponseEntity.ok().body(address.getId());
    }
}
