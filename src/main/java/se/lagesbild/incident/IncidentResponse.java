package se.lagesbild.incident;

import java.time.LocalDateTime;

public record IncidentResponse(
        Long id,
        IncidentType type,
        String typeLabel,
        IncidentKind kind,
        String area,
        double lat,
        double lng,
        int severity,
        LocalDateTime occurredAt,
        String description,
        Actor reportedBy,
        String reportedByLabel
) {
    public static IncidentResponse from(Incident i) {
        return new IncidentResponse(
                i.getId(),
                i.getType(),
                i.getType().getLabel(),
                i.getKind(),
                i.getArea(),
                i.getLat(),
                i.getLng(),
                i.getSeverity(),
                i.getOccurredAt(),
                i.getDescription(),
                i.getReportedBy(),
                i.getReportedBy().getLabel()
        );
    }
}
