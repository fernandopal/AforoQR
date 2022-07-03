package es.fernandopal.aforoqr.api.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchRequest {
    private String search;
    private SearchType type;
}
