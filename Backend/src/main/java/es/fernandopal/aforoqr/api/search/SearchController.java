package es.fernandopal.aforoqr.api.search;

import es.fernandopal.aforoqr.api.event.Event;
import es.fernandopal.aforoqr.api.exception.ApiBadRequestException;
import es.fernandopal.aforoqr.api.room.Room;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @PostMapping()
    @Operation(description = "Search for an event or room by different types of metadata")
    public ResponseEntity<Tuple<Set<Room>, Set<Event>>> search(@RequestBody SearchRequest searchRequest) {

        SearchType searchType = searchRequest.getType();
        String search = searchRequest.getSearch();

        Tuple<List<Room>, List<Event>> obj = new Tuple<>(new ArrayList<>(), new ArrayList<>());
        if (!search.isBlank()) {
            switch (searchType) {
                case ROOM_ID -> searchEventById(search, obj);

                case EVENT_ID -> searchRoomById(search, obj);

                case ROOM_NAME -> obj.rooms.addAll(searchService.searchRoomsByKeywords(search));

                case EVENT_NAME -> obj.events.addAll(searchService.searchEventsByKeywords(search));

                case UNKNOWN -> fullSearch(search, obj);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Tuple<>(Set.copyOf(obj.rooms), Set.copyOf(obj.events)));
    }

    private void searchEventById(String search, Tuple<List<Room>, List<Event>> obj) {
        long id;
        try {
            id = Long.parseLong(search);
        } catch (Exception e) {
            throw new ApiBadRequestException("El término de búsequeda no es una id válida");
        }

        searchService.searchEventById(id).ifPresent(obj.events::add);
    }

    private void searchRoomById(String search, Tuple<List<Room>, List<Event>> obj) {
        long id;
        try {
            id = Long.parseLong(search);
        } catch (Exception e) {
            throw new ApiBadRequestException("El término de búsequeda no es una id válida");
        }

        searchService.searchRoomById(id).ifPresent(obj.rooms::add);
    }

    private void fullSearch(String search, Tuple<List<Room>, List<Event>> obj) {
        long id;
        try {
            id = Long.parseLong(search);
        } catch (Exception e) {
            obj.rooms.addAll(searchService.searchRoomsByKeywords(search));
            obj.events.addAll(searchService.searchEventsByKeywords(search));
            return;
        }

        searchService.searchRoomById(id).ifPresent(obj.rooms::add);
        searchService.searchEventById(id).ifPresent(obj.events::add);
        obj.rooms.addAll(searchService.searchRoomsByKeywords(search));
        obj.events.addAll(searchService.searchEventsByKeywords(search));
    }

    private record Tuple<K, V>(K rooms, V events) {}
}
