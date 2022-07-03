package es.fernandopal.aforoqr.api.reservation;

import es.fernandopal.aforoqr.api.Constants;
import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.appuser.AppUserRepository;
import es.fernandopal.aforoqr.api.email.EmailSender;
import es.fernandopal.aforoqr.api.email.EmailTemplates;
import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.event.EventRepository;
import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.exception.ApiNotFoundException;
import es.fernandopal.aforoqr.api.reservation.token.ConfirmationToken;
import es.fernandopal.aforoqr.api.reservation.token.ConfirmationTokenRepository;
import es.fernandopal.aforoqr.api.reservation.token.ConfirmationTokenService;
import es.fernandopal.aforoqr.api.room.Room;
import es.fernandopal.aforoqr.api.room.RoomRepository;
import es.fernandopal.aforoqr.api.security.SecurityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static es.fernandopal.aforoqr.api.reservation.ReservationBuilder.reservation;

@Service
@AllArgsConstructor
public class ReservationService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReservationService.class);
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final ReservationRepository reservationRepository;
    private final AppUserRepository appUserRepository;
    private final SecurityService securityService;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final EmailSender emailSender;

    // fpalomo - Obtiene una lista de todas las reservas que hay en la base de datos
    public List<Reservation> getAll() {
//        securityService.preventAccess(!securityService.isAdmin(), "ReservationService.getAll");

        return reservationRepository.findAll();
    }

    // fpalomo - Guarda una reserva
    public void save(Reservation reservation, AppUser appUser) {
        if (appUser != null) {
            AppUser userQuery = appUserRepository.findAppUserByUid(appUser.getUid()).orElse(null);
            if (userQuery == null) appUserRepository.save(appUser);
        }

        if (reservation != null) {
            boolean reservationAlreadyExists = !reservationRepository.findReservations(reservation.getUser(), reservation.getRoom(), reservation.getEvent(), reservation.getStartDate(), reservation.getEndDate()).isEmpty();
            if (!reservationAlreadyExists) reservationRepository.save(reservation);
        }
    }

    // fpalomo - Obtiene una reserva
    public Optional<Reservation> getById(Long id) {
        return doReservationChecks(id, ReservationAction.GET, null, false).reservation;
    }

    // fpalomo - Envía un email de confirmación para la reserva especificada
    public void sendConfirmationEmail(Reservation reservation, @Nullable AppUser appUser) {
        if (appUser == null) {
            appUser = securityService.getUser();
        }

        ConfirmationToken confirmationToken = getConfirmationToken(reservation);

        String emailBody = EmailTemplates.confirmReservationEmail()
                .replace("{room}", reservation.getRoom().getAddress().toString())
                .replace("{time}", (reservation.getStartDate() != null) ? reservation.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "null")
                .replace("{link}", Constants.APP_URL + "/reservation/" + reservation.getId() + "/confirm/" + confirmationToken.getToken());

        emailSender.send(appUser.getEmail(), "Confirmación de tu reserva", emailBody);
    }

    public void sendConfirmationEmail(Long id, @Nullable AppUser appUser) {
        Reservation reservation = getById(id).orElse(null);
        sendConfirmationEmail(reservation, appUser);
    }

    // fpalomo - Recupera el token activo para una reserva o genera uno nuevo si no hay
    public ConfirmationToken getConfirmationToken(Reservation reservation) {
        ConfirmationToken confirmationToken = confirmationTokenService.getActiveTokenForReservation(reservation.getId()).orElse(null);

        if ( confirmationToken == null) {
            confirmationToken = new ConfirmationToken(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(Constants.CONFIRMATION_TOKENS_DURATION),
                    reservation
            );

            confirmationTokenService.save(confirmationToken);
        }

        return confirmationToken;
    }

    // fpalomo - Cmprueba si una reserva no ha sido confirmada o cancelada y si pertenece al usuario, en caso afirmativo, la confirma
    public void confirmReservation(Long id, String token) {
        doReservationChecks(id, ReservationAction.CONFIRM, null, true);
        confirmationTokenService.confirmToken(token);
    }

    // fpalomo - Obtiene una lista de todas las reservas asociadas a un usuario en concreto
    public List<Reservation> getReservationsForUser(String uid) {
        return reservationRepository.findReservationByUserUid(uid);
    }

    // fpalomo - Devuelve una lista de reservas según lo que nos venga en la request
    public List<Reservation> getReservations(ReservationRequest reservationRequest) {
//        AppUser appUser = doReservationChecks(null, ReservationAction.GET, reservationRequest, false).appUser;
        doReservationChecks(null, ReservationAction.GET, reservationRequest, false);
        Room room = roomRepository.findRoomById(reservationRequest.getRoomId()).orElse(null);
        Event event = eventRepository.findEventById(reservationRequest.getEventId()).orElse(null);
        LocalDateTime startDate = reservationRequest.getStartDate();
        LocalDateTime endDate = reservationRequest.getEndDate();

        LOGGER.info(reservationRequest.toString());

        if (room == null && event == null) {
            throw new ApiBadRequestException("No se ha especificado una id de evento o sala válida");
        }

//        if (appUser != null && ((startDate != null && endDate != null) || ((room != null || event != null) && endDate == null && startDate == null))) {
//            if (startDate != null && endDate != null) {
//                return reservationRepository.findReservations(appUser, room, event, startDate, endDate);
//            }
//            return reservationRepository.findReservations(appUser, room, event);
//        }

        if (startDate != null && endDate != null) {
            return reservationRepository.findReservations(room, event, startDate, endDate);
        }

        if (startDate != null) {
            return reservationRepository.findReservations(room, event, startDate);
        }

        return reservationRepository.findReservations(room, event);
    }

    // fpalomo - Comprueba si una reserva no ha sido cancelada y si pertenece al usuario que hace la petición, en caso afirmativo, la cancela
    public void cancelReservation(Long id) {
        doReservationChecks(id, ReservationAction.CANCEL, null, false);
        reservationRepository.cancelReservation(id, LocalDateTime.now());
    }

    // fpalomo - Recibe los datos para crear una nueva reserva (independientemente de si es para un evento o una sala), la genera y la guarda en la base de datos
    public Reservation createReservation(ReservationRequest reservationRequest) {
        AppUser appUser = doReservationChecks(null, ReservationAction.CREATE, reservationRequest, false).appUser;
        Room room = roomRepository.findRoomById(reservationRequest.getRoomId()).orElse(null);
        Event event = eventRepository.findEventById(reservationRequest.getEventId()).orElse(null);

        if (room == null && event == null) {
            throw new ApiBadRequestException("Para realizar una reserva se necesita una id de sala o evento válida");
        }

        // fpalomo - Se ha optado por usar el patrón de diseño "fluent interface"
        Reservation reservation = reservation()
                .withRequestDate(LocalDateTime.now())
                .withUser(appUser)
                .withRoom(room)
                .withEvent(event)
                .withStartDate(reservationRequest.getStartDate())
                .withEndDate(reservationRequest.getEndDate())
                .build();

//        Al persistir el token también se guarda la reserva, si guardamos antes la reserva da error
//        this.save(reservation, securityService.getUser());

        // fpalomo 08/05/2022 - Enviamos el email de confirmación al usuario
        sendConfirmationEmail(reservation, appUser);

        return reservation;
    }

    // fpalomo - Elimina una reserva de la base de datos
    public void deleteReservation(Long id) {
        Reservation reservation = doReservationChecks(id, ReservationAction.DELETE, null, false).reservation.orElse(null);

        if (reservation != null) {
            ConfirmationToken confirmationToken = confirmationTokenService.getActiveTokenForReservation(id).orElse(null);

            if (confirmationToken != null) {
                confirmationToken.setReservation(null);
                confirmationTokenRepository.save(confirmationToken);
            }

            reservationRepository.delete(reservation);
        }
    }

    // fpalomo - Utilidad para realizar comprobaciones sobre las llamadas al ReservationService
    private Tuple<Optional<Reservation>, AppUser> doReservationChecks(Long id, ReservationAction action, ReservationRequest request, Boolean simplifyErrors) {
        Reservation reservation = null;

        if (!action.equals(ReservationAction.CREATE) && id != null) {
            reservation = reservationRepository.findReservationById(id).orElse(null);
            if (reservation == null) {
                throw new ApiNotFoundException("La id introducida no pertenece a ninguna reserva", simplifyErrors);
            }

            if (!action.equals(ReservationAction.CONFIRM)) {
                boolean isAdmin = securityService.getUser().isAdmin();
                boolean isOwner = securityService.getUser().getUid().equals(reservation.getUser().getUid());

                securityService.preventAccess(!isAdmin && !isOwner);
            }
        }

        AppUser appUser = null;
        if (!action.equals(ReservationAction.CONFIRM)) {
            appUser = (request == null) ? securityService.getUser() : appUserRepository.findAppUserByUid(request.getUserId()).orElse(null);
            if (appUser == null) {
                if (action.equals(ReservationAction.CREATE)) {
                    throw new ApiBadRequestException("La id no pertenece a ningún usuario", simplifyErrors);
                }
                throw new ApiBadRequestException("Sesión inválida, por favor inicia sesión de nuevo", simplifyErrors);
            }

            boolean isAdmin = securityService.getUser().isAdmin();
            boolean isOwner = securityService.getUser().getUid().equals(appUser.getUid());

            securityService.preventAccess(!isAdmin && !isOwner);
        }

        if (!action.equals(ReservationAction.CREATE) && reservation != null) {
//            if (!Objects.equals(reservation.getUser().getUid(), appUser.getUid()) && !securityService.isAdmin()) {
//                throw new ApiUnauthorizedException("Esta reserva pertenece a otro usuario y no puedes acceder a ella");
//            }

            if (reservation.isCancelled() && action.equals(ReservationAction.CANCEL)) {
                throw new ApiBadRequestException("Esta reserva ya ha sido cancelada", simplifyErrors);
            }

            if (action.equals(ReservationAction.CONFIRM)) {
                if (reservation.isCancelled()) {
                    throw new ApiBadRequestException("No puedes confirmar una reserva que ha sido cancelada", simplifyErrors);
                }
                if (reservation.isConfirmed()) {
                    throw new ApiBadRequestException("Esta reserva ya ha sido confirmada", simplifyErrors);
                }
            }

            if (action.equals(ReservationAction.DELETE) && reservation.isConfirmed()) {
                throw new ApiBadRequestException("No puedes eliminar una reserva que ha sido confirmada", simplifyErrors);
            }

        }

        return new Tuple<>(Optional.ofNullable(reservation), appUser);
    }

    // fpalomo - <Reservation, AppUser>
    private record Tuple<K, V>(K reservation, V appUser) {}

    public Integer getReservationCount() {
        return reservationRepository.getTotalReservations();
    }
}
