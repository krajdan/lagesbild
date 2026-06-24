package se.lagesbild.intervention;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import se.lagesbild.incident.Actor;

import java.time.Instant;
import java.time.LocalDate;

/**
 * En dokumenterad insats kopplad till ett omrade och en orsak. Insatser kan
 * foljas upp mot lagesbilden for att se om problemen faktiskt minskar.
 */
@Entity
@Table(name = "intervention")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(length = 600)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterventionStatus status;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Actor owner;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Intervention() {
        // JPA
    }

    public Intervention(String title, String area, String description,
                        InterventionStatus status, LocalDate startedAt, Actor owner) {
        this.title = title;
        this.area = area;
        this.description = description;
        this.status = status;
        this.startedAt = startedAt;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArea() {
        return area;
    }

    public String getDescription() {
        return description;
    }

    public InterventionStatus getStatus() {
        return status;
    }

    public LocalDate getStartedAt() {
        return startedAt;
    }

    public Actor getOwner() {
        return owner;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
