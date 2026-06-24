package se.lagesbild.incident;

/**
 * EMBRACE skiljer pa faktiska brott och otrygghetsskapande
 * handelser/platser (upplevd otrygghet som inte alltid polisanmals).
 */
public enum IncidentKind {
    BROTT("Brott"),
    OTRYGGHET("Otrygghet");

    private final String label;

    IncidentKind(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
