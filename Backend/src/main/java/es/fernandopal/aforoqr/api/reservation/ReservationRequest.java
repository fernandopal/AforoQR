package es.fernandopal.aforoqr.api.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationRequest {
    private String userId;
    private Long eventId;
    private Long roomId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Override
    public String toString() {
        return "ReservationRequest{" +
                "userId='" + userId + '\'' +
                ", eventId=" + eventId +
                ", roomId=" + roomId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
