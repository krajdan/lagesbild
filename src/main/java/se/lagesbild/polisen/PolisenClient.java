package se.lagesbild.polisen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import se.lagesbild.incident.IncidentKind;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Hamtar riktiga handelser fran Polisens oppna API och cachar dem i minnet.
 *
 * Polisens API har harda anropsgranser (minst 10 s mellan anrop, max 60/timme,
 * 1440/dygn) sa vi pollar bara var 15:e minut och serverar fran cache. Faller
 * natet bort behaller vi senaste lyckade hamtning och markerar flodet som offline.
 */
@Component
public class PolisenClient {

    private static final Logger log = LoggerFactory.getLogger(PolisenClient.class);

    // Polisen anger en mittkoordinat for kommunen, inte exakt brottsplats.
    private static final String API_URL = "https://polisen.se/api/events?locationname={loc}";
    private static final String LOCATION = "Örebro";

    private static final DateTimeFormatter POLISEN_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");

    // Handelsetyper som vi klassar som otrygghet snarare an brott.
    private static final Set<String> OTRYGGHET_TYPES = Set.of(
            "Ofog barn/ungdom", "Ofredande/förargelse", "Fylleri/LOB",
            "Bråk", "Ordningslagen", "Skadegörelse"
    );

    private final RestClient restClient = RestClient.builder()
            .requestFactory(ClientHttpRequestFactories.get(
                    ClientHttpRequestFactorySettings.DEFAULTS
                            .withConnectTimeout(Duration.ofSeconds(5))
                            .withReadTimeout(Duration.ofSeconds(8))))
            .defaultHeader("User-Agent", "Lagesbild-Demo/0.1 (brottsforebyggande prototyp)")
            .build();

    private volatile List<PolisenEvent> cache = List.of();
    private volatile Instant lastUpdated = null;
    private volatile boolean live = false;

    /** Korar direkt vid uppstart (initialDelay 0) och sedan var 15:e minut. */
    @Scheduled(initialDelay = 0, fixedRate = 15 * 60 * 1000)
    public void refresh() {
        try {
            PolisenApiModels.RawEvent[] raw = restClient.get()
                    .uri(API_URL, LOCATION)
                    .retrieve()
                    .body(PolisenApiModels.RawEvent[].class);

            if (raw == null) {
                log.warn("Polisen-API gav tomt svar");
                return;
            }

            List<PolisenEvent> mapped = java.util.Arrays.stream(raw)
                    .map(this::map)
                    .filter(java.util.Objects::nonNull)
                    .sorted(Comparator.comparing(PolisenEvent::occurredAt).reversed())
                    .toList();

            cache = mapped;
            lastUpdated = Instant.now();
            live = true;
            log.info("Hamtade {} handelser fran Polisens API", mapped.size());
        } catch (Exception e) {
            live = false;
            log.warn("Kunde inte hamta fran Polisens API: {}", e.getMessage());
        }
    }

    public List<PolisenEvent> events() {
        return cache;
    }

    public Instant lastUpdated() {
        return lastUpdated;
    }

    public boolean isLive() {
        return live;
    }

    private PolisenEvent map(PolisenApiModels.RawEvent e) {
        double[] gps = parseGps(e.location() != null ? e.location().gps() : null);
        if (gps == null) {
            return null; // utan koordinat kan vi inte placera handelsen pa kartan
        }
        IncidentKind kind = e.type() != null && OTRYGGHET_TYPES.contains(e.type())
                ? IncidentKind.OTRYGGHET
                : IncidentKind.BROTT;

        return new PolisenEvent(
                e.id(),
                e.name(),
                e.summary(),
                e.type() != null ? e.type() : "Handelse",
                kind,
                e.location() != null ? e.location().name() : LOCATION,
                gps[0],
                gps[1],
                parseDateTime(e.datetime()),
                e.url() != null ? "https://polisen.se" + e.url() : null,
                true
        );
    }

    private double[] parseGps(String gps) {
        if (gps == null || gps.isBlank()) {
            return null;
        }
        try {
            String[] parts = gps.split(",");
            return new double[]{Double.parseDouble(parts[0].trim()), Double.parseDouble(parts[1].trim())};
        } catch (Exception ex) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String dt) {
        if (dt == null) {
            return LocalDateTime.now();
        }
        try {
            return OffsetDateTime.parse(dt, POLISEN_DATETIME).toLocalDateTime();
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }
}
