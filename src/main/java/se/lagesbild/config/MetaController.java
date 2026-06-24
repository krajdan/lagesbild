package se.lagesbild.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.lagesbild.incident.Actor;
import se.lagesbild.incident.IncidentType;
import se.lagesbild.intervention.InterventionStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Levererar metadata (handelsetyper, aktorer, omraden, statusar) sa att
 * granssnittet kan bygga sina menyer utan att harkoda varden.
 */
@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @GetMapping
    public Map<String, Object> meta() {
        List<Map<String, String>> types = Arrays.stream(IncidentType.values())
                .map(t -> Map.of(
                        "value", t.name(),
                        "label", t.getLabel(),
                        "kind", t.getKind().name()
                ))
                .toList();

        List<Map<String, String>> actors = Arrays.stream(Actor.values())
                .map(a -> Map.of("value", a.name(), "label", a.getLabel()))
                .toList();

        List<Map<String, String>> statuses = Arrays.stream(InterventionStatus.values())
                .map(s -> Map.of("value", s.name(), "label", s.getLabel()))
                .toList();

        return Map.of(
                "areas", AreaCatalog.names(),
                "types", types,
                "actors", actors,
                "interventionStatuses", statuses
        );
    }
}
