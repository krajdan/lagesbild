package se.lagesbild.intervention;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import se.lagesbild.incident.Actor;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

    private final InterventionRepository repository;

    public InterventionController(InterventionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Response> list() {
        return repository.findByOrderByStartedAtDesc().stream().map(Response::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response create(@Valid @RequestBody Request req) {
        Intervention saved = repository.save(new Intervention(
                req.title().trim(),
                req.area().trim(),
                req.description(),
                req.status(),
                req.startedAt(),
                req.owner()
        ));
        return Response.from(saved);
    }

    public record Request(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 80) String area,
            @Size(max = 600) String description,
            @NotNull InterventionStatus status,
            @NotNull LocalDate startedAt,
            @NotNull Actor owner
    ) {
    }

    public record Response(
            Long id,
            String title,
            String area,
            String description,
            InterventionStatus status,
            String statusLabel,
            LocalDate startedAt,
            Actor owner,
            String ownerLabel
    ) {
        static Response from(Intervention i) {
            return new Response(
                    i.getId(),
                    i.getTitle(),
                    i.getArea(),
                    i.getDescription(),
                    i.getStatus(),
                    i.getStatus().getLabel(),
                    i.getStartedAt(),
                    i.getOwner(),
                    i.getOwner().getLabel()
            );
        }
    }
}
