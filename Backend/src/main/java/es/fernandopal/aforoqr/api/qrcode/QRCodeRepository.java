package es.fernandopal.aforoqr.api.qrcode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QRCodeRepository extends JpaRepository<QRCode, Long> {
    Optional<QRCode> findQRCodeById(Long id);
    Optional<QRCode> findQRCodeByRoomId(Long roomId);
    Optional<QRCode> findQRCodeByEventId(Long eventId);
}
