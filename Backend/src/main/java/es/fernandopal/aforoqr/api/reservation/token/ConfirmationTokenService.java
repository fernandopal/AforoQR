package es.fernandopal.aforoqr.api.reservation.token;

import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.reservation.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ReservationRepository reservationRepository;

    public void save(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public Optional<ConfirmationToken> getActiveTokenForReservation(Long reservationId) {
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAllByReservation(reservationId);
        Optional<ConfirmationToken> confirmationToken = Optional.empty();

        for (ConfirmationToken cToken : confirmationTokens) {
            if (cToken.getConfirmedAt() == null && !cToken.getExpiresAt().isBefore(LocalDateTime.now())) {
                confirmationToken = Optional.of(cToken);
                break;
            }
        }

        return confirmationToken;
    }

    public List<ConfirmationToken> getAllTokensForReservation(Long reservationId) {
        return confirmationTokenRepository.findAllByReservation(reservationId);
    }

    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = getToken(token).orElseThrow(() -> new ApiBadRequestException("No se ha encontrado el token", true));

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (confirmationToken.getConfirmedAt() != null || expiredAt.isBefore(LocalDateTime.now())) {
            throw new ApiBadRequestException("Token inválido", true);
        }

        setConfirmedAt(token);
        reservationRepository.confirmReservation(confirmationToken.getReservation().getId(), LocalDateTime.now());
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}