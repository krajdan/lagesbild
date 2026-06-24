package se.lagesbild.incident;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private IncidentType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IncidentKind kind;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    /** Allvarlighetsgrad 1-3 (1 = lag, 3 = hog). */
    @Column(nullable = false)
    private int severity;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Actor reportedBy;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Incident() {
        // JPA
    }

    public Incident(IncidentType type, String area, double lat, double lng,
                    int severity, LocalDateTime occurredAt, String description, Actor reportedBy) {
        this.type = type;
        this.kind = type.getKind();
        this.area = area;
        this.lat = lat;
        this.lng = lng;
        this.severity = severity;
        this.occurredAt = occurredAt;
        this.description = description;
        this.reportedBy = reportedBy;
    }

    public Long getId() {
        return id;
    }

    public IncidentType getType() {
        return type;
    }

    public IncidentKind getKind() {
        return kind;
    }

    public String getArea() {
        return area;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getSeverity() {
        return severity;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getDescription() {
        return description;
    }

    public Actor getReportedBy() {
        return reportedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
