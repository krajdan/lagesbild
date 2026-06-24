package se.lagesbild.polisen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Ravastrukturer som speglar Polisens oppna API (/api/events).
 * Vi bryr oss bara om ett urval av falten.
 */
public final class PolisenApiModels {

    private PolisenApiModels() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawEvent(
            long id,
            String datetime,
            String name,
            String summary,
            String url,
            String type,
            RawLocation location
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RawLocation(
            String name,
            String gps
    ) {
    }
}
