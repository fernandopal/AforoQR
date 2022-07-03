package es.fernandopal.aforoqr.api.reservation;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findReservationById(Long id);
    List<Reservation> findReservationByUserUid(String uid);

    @Query("SELECT r FROM Reservation r WHERE r.user = ?1 AND (r.room = ?2 OR r.event = ?3) AND r.cancelled = false")
    List<Reservation> findReservations(AppUser appUser, Room room, Event event);

    @Query("SELECT r FROM Reservation r WHERE r.user = ?1 AND (r.room = ?2 OR r.event = ?3) AND (r.startDate <= ?4 OR ?4 IS NULL) AND (r.endDate <= ?5 OR ?5 IS NULL) AND r.cancelled = false")
    List<Reservation> findReservations(AppUser appUser, Room room, Event event, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT r FROM Reservation r WHERE (r.room = ?1 OR r.event = ?2) AND r.cancelled = false")
    List<Reservation> findReservations(Room room, Event event);

    @Query("SELECT r FROM Reservation r WHERE (r.room = ?1 OR r.event = ?2) AND (r.startDate <= ?3 OR ?3 IS NULL) AND r.cancelled = false AND r.confirmed = true")
    List<Reservation> findReservations(Room room, Event event, LocalDateTime date);

    @Query("SELECT r FROM Reservation r WHERE (r.room = ?1 OR r.event = ?2) AND (r.startDate <= ?3 OR ?3 IS NULL) AND (r.endDate <= ?4 OR ?4 IS NULL) AND r.cancelled = false")
    List<Reservation> findReservations(Room room, Event event, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.cancelled = true, r.cancellationDate = ?2 WHERE r.id = ?1 ")
    void cancelReservation(Long id, LocalDateTime cancellationDate);

    @Modifying
    @Transactional
    @Query("UPDATE Reservation r SET r.confirmed = true, r.confirmationDate = ?2 WHERE r.id = ?1 ")
    void confirmReservation(Long id, LocalDateTime confirmationDate);

    @Query("SELECT COUNT(reservation.id) FROM Reservation reservation")
    Integer getTotalReservations();
}
