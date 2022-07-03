package es.fernandopal.aforoqr.api.qrcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QRCodeRequest {
    private Long eventId;
    private Long roomId;

    @Override
    public String toString() {
        return "QRCodeRequest{" +
                "eventId=" + eventId +
                ", roomId=" + roomId +
                '}';
    }
}