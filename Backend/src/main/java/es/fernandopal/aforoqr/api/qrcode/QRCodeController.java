package es.fernandopal.aforoqr.api.qrcode;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/qr")
public class QRCodeController {

    private final static Logger LOGGER = LoggerFactory.getLogger(QRCodeController.class);
    private QRCodeService qrCodeService;

    @GetMapping
    @Operation(description = "Returns a list of all the stored QRCode records in the database")
    public ResponseEntity<List<QRCode>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(qrCodeService.getAll());
    }

    @GetMapping(value = "/{id}")
    @Operation(description = "Returns all the data stored in the database about a QRCode")
    public ResponseEntity<QRCode> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(qrCodeService.getById(id).orElse(null));
    }

    @ResponseBody
    @Operation(description = "Returns the raw image of the QRCode")
    @GetMapping(value = "/{id}/raw", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable("id") Long id) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename(id + ".png").build());

        return ResponseEntity.ok().headers(headers).body(qrCodeService.getQRCodeImage(id));
    }

    @GetMapping(value = "/room/{roomId}")
    @Operation(description = "Gets the QRCode associated to a room")
    public ResponseEntity<QRCode> getByRoomId(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(qrCodeService.getByRoom(roomId).orElse(null));
    }

    @GetMapping(value = "/event/{eventId}")
    @Operation(description = "Gets the QRCode associated to an event")
    public ResponseEntity<QRCode> getByEventId(@PathVariable("eventId") Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(qrCodeService.getByEvent(eventId).orElse(null));
    }

    @PostMapping(value = "/generate")
    @Operation(description = "Generate a QRCode")
    public ResponseEntity<QRCode> generate(@RequestBody QRCodeRequest qrCodeRequest) {
        return ResponseEntity.ok().body(qrCodeService.generateQRCode(qrCodeRequest));
    }

    @PostMapping(value = "/{id}/delete")
    @Operation(description = "Delete a QRCode")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {
        qrCodeService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}