package es.fernandopal.aforoqr.api.qrcode;

import es.fernandopal.aforoqr.api.Constants;
import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.event.EventRepository;
import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.exception.ApiInternalServerErrorException;
import es.fernandopal.aforoqr.api.exception.ApiNotFoundException;
import es.fernandopal.aforoqr.api.room.Room;
import es.fernandopal.aforoqr.api.room.RoomRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QRCodeService {

    private final QRCodeRepository qrCodeRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;

    public List<QRCode> getAll() {
        return qrCodeRepository.findAll();
    }

    public QRCode save(QRCode qrCode) {
        return qrCodeRepository.save(qrCode);
    }
    public void delete(Long id) {
        QRCode qrCode = getById(id).orElse(null);

        if (qrCode == null) throw new ApiNotFoundException("No se ha encontrado ningún QR asociado a esa ID", true);

        qrCodeRepository.delete(qrCode);
    }

    public Optional<QRCode> getByRoom(Long roomId) {
        return qrCodeRepository.findQRCodeByRoomId(roomId);
    }
    public Optional<QRCode> getByEvent(Long eventId) {
        return qrCodeRepository.findQRCodeByEventId(eventId);
    }
    public Optional<QRCode> getById(Long id) {
        return qrCodeRepository.findQRCodeById(id);
    }

    public byte[] getQRCodeImage(Long id) throws IOException {
        final Path path = FileSystems.getDefault().getPath(Constants.QR_STORAGE_PATH + "/" + id + ".png");
        return IOUtils.toByteArray(path.toUri());
    }

    public QRCode generateQRCode(QRCodeRequest qrCodeRequest) {
        return generateQRCode(qrCodeRequest.getRoomId(), qrCodeRequest.getEventId());
    }

    public QRCode generateQRCode(Long roomId, Long eventId) {
        if(roomId == null && eventId == null) {
            throw new ApiBadRequestException("La petición no se ha realizado con los parámetros requeridos", true);
        }

        Room room = roomRepository.findRoomById(roomId).orElse(null);
        Event event = eventRepository.findEventById(eventId).orElse(null);

        try {
            QRCode qrCode = (room == null) ? getByEvent(eventId).orElse(null) : getByRoom(roomId).orElse(null);

            if (qrCode == null) {
                qrCode = save(new QRCode(null, room, event));
            }

            final Path imagePath = new QRCodeGenerator(qrCode.getId()).generate();

            qrCode.setImagePath(imagePath.toString());

            return save(qrCode);
        } catch (Exception e) {
            throw new ApiInternalServerErrorException(e.getMessage(), true);
        }
    }
}
