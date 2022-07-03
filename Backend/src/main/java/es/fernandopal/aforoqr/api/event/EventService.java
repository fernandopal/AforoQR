package es.fernandopal.aforoqr.api.event;

import es.fernandopal.aforoqr.api.appuser.AppUser;
import es.fernandopal.aforoqr.api.appuser.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final AppUserRepository appUserRepository;

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public void save(Event event, AppUser appUser) {
        boolean reservationAlreadyExists = !eventRepository.findEvent(event.getRoom(), event.getStartDate(), event.getEndDate()).isEmpty();
        eventRepository.save(event);
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findEventById(id);
    }

    public Integer getEventCount() {
        return eventRepository.getTotalEvents();
    }
}
