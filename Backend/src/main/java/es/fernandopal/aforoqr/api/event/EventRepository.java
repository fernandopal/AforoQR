package es.fernandopal.aforoqr.api.event;

import es.fernandopal.aforoqr.api.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long id);
    List<Event> findByName(String name);

    // TODO: INCLUIR OWNERS & PUBLIC/PRIVATE
    @Query("SELECT e FROM Event e WHERE e.room = ?1 AND e.startDate = ?2 AND e.endDate = ?3")
    List<Event> findEvent(Room room, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT e FROM Event e WHERE e.name LIKE concat('%', ?1, '%') OR e.room.code LIKE concat('%', ?1, '%') OR e.room.name LIKE concat('%', ?1, '%') OR e.room.address.country LIKE concat('%', ?1, '%') OR e.room.address.city LIKE concat('%', ?1, '%') OR e.room.address.street LIKE concat('%', ?1, '%') OR e.room.address.buildingNumber LIKE concat('%', ?1, '%')")
    List<Event> findByKeywords(String keywords);

    @Query("SELECT COUNT(event.id) FROM Event event")
    Integer getTotalEvents();
}