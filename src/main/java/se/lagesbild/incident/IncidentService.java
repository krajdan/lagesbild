package se.lagesbild.incident;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository repository;

    public IncidentService(IncidentRepository repository) {
        this.repository = repository;
    }

    public List<Incident> findAll() {
        return repository.findByOrderByOccurredAtDesc();
    }

    /**
     * Hamtar handelser filtrerade pa omrade, typ av handelse (brott/otrygghet)
     * och tidsintervall. Filtrering sker i tjanstelagret for att halla
     * datalager-koden enkel; for en demo med hundratals rader racker det gott.
     */
    public List<Incident> filter(String area, IncidentKind kind, LocalDateTime from, LocalDateTime to) {
        return repository.findByOrderByOccurredAtDesc().stream()
                .filter(i -> area == null || area.equalsIgnoreCase(i.getArea()))
                .filter(i -> kind == null || kind == i.getKind())
                .filter(i -> from == null || !i.getOccurredAt().isBefore(from))
                .filter(i -> to == null || !i.getOccurredAt().isAfter(to))
                .toList();
    }

    public Incident create(IncidentRequest req) {
        Incident incident = new Incident(
                req.type(),
                req.area().trim(),
                req.lat(),
                req.lng(),
                req.severity(),
                req.occurredAt(),
                req.description(),
                req.reportedBy()
        );
        return repository.save(incident);
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
