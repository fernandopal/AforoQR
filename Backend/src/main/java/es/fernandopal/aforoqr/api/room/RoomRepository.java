package es.fernandopal.aforoqr.api.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findRoomById(Long id);
    List<Room> findByName(String name);
    Optional<Room> findByCode(String code);

    @Query("SELECT r FROM Room r WHERE r.name LIKE concat('%', ?1, '%') OR r.code LIKE concat('%', ?1, '%') OR r.address.country LIKE concat('%', ?1, '%') OR r.address.city LIKE concat('%', ?1, '%') OR r.address.street LIKE concat('%', ?1, '%') OR r.address.buildingNumber LIKE concat('%', ?1, '%')")
    List<Room> findByKeywords(String keywords);

    @Query("SELECT COUNT(room.id) FROM Room room")
    Integer getTotalRooms();
}
