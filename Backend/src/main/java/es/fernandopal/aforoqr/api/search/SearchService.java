package es.fernandopal.aforoqr.api.search;

import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.event.EventRepository;
import es.fernandopal.aforoqr.api.room.Room;
import es.fernandopal.aforoqr.api.room.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SearchService {

    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;


    public List<Room> searchRoomsByKeywords(String keywords) {
        return roomRepository.findByKeywords(keywords);
    }

    public List<Event> searchEventsByKeywords(String keywords) {
        return eventRepository.findByKeywords(keywords);
    }

    public Optional<Room> searchRoomById(Long id) {
        return roomRepository.findRoomById(id);
    }

    public Optional<Event> searchEventById(Long id) {
        return eventRepository.findEventById(id);
    }
}
