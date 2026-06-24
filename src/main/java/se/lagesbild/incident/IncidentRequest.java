package se.lagesbild.incident;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Inkommande rapport. En handelse ska kunna rapporteras pa under 90 sekunder,
 * sa antalet falt halls medvetet litet.
 */
public record IncidentRequest(

        @NotNull(message = "Valj handelsetyp")
        IncidentType type,

        @NotBlank(message = "Ange omrade")
        @Size(max = 80)
        String area,

        @NotNull(message = "Saknar latitud")
        Double lat,

        @NotNull(message = "Saknar longitud")
        Double lng,

        @Min(value = 1, message = "Allvarlighetsgrad 1-3")
        @Max(value = 3, message = "Allvarlighetsgrad 1-3")
        int severity,

        @NotNull(message = "Ange nar handelsen intraffade")
        LocalDateTime occurredAt,

        @Size(max = 500)
        String description,

        @NotNull(message = "Ange rapporterande aktor")
        Actor reportedBy
) {
}
