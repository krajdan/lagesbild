package se.lagesbild.polisen;

import se.lagesbild.incident.IncidentKind;

import java.time.LocalDateTime;

/**
 * En polishandelse normaliserad till vart eget format. Markt med kalla och
 * en flagga for att platsen ar ungefarlig (Polisen anger en mittkoordinat
 * for kommunen/lanet, inte exakt brottsplats).
 */
public record PolisenEvent(
        long id,
        String name,
        String summary,
        String typeLabel,
        IncidentKind kind,
        String locationName,
        double lat,
        double lng,
        LocalDateTime occurredAt,
        String url,
        boolean approximateLocation
) {
}
