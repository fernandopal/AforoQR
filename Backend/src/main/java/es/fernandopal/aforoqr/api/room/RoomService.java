package es.fernandopal.aforoqr.api.room;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.appuser.AppUserRepository;
import es.fernandopal.aforoqr.api.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AppUserRepository appUserRepository;
    private final SecurityService securityService;

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public void save(Room room, AppUser appUser) {
        if (appUser != null) appUserRepository.save(appUser);
        roomRepository.save(room);
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findRoomById(id);
    }
    public Optional<Room> getRoom(String code) {
        return roomRepository.findByCode(code);
    }
    public List<Room> getRoomsByName(String name) {
        return roomRepository.findByName(name);
    }

    public Integer getRoomCount() {
        return roomRepository.getTotalRooms();
    }

    public void delete(Long id) {
        securityService.preventAccess(!securityService.getUser().isAdmin());
        roomRepository.deleteById(id);
    }
}