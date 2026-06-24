package se.lagesbild.incident;

/**
 * Handelsetyp. Varje typ ar klassad som brott eller otrygghet sa att
 * lagesbilden kan visa bade anmald brottslighet och upplevd otrygghet.
 */
public enum IncidentType {
    MISSHANDEL("Misshandel", IncidentKind.BROTT),
    STOLD("Stold", IncidentKind.BROTT),
    INBROTT("Inbrott", IncidentKind.BROTT),
    SKADEGORELSE("Skadegorelse", IncidentKind.BROTT),
    NARKOTIKABROTT("Narkotikabrott", IncidentKind.BROTT),
    HOT("Hot", IncidentKind.BROTT),

    KLOTTER("Klotter", IncidentKind.OTRYGGHET),
    NEDSKRAPNING("Nedskrapning", IncidentKind.OTRYGGHET),
    BRISTANDE_BELYSNING("Bristande belysning", IncidentKind.OTRYGGHET),
    OLAGA_SAMLING("Storande folksamling", IncidentKind.OTRYGGHET),
    UPPLEVD_OTRYGGHET("Upplevd otrygghet", IncidentKind.OTRYGGHET);

    private final String label;
    private final IncidentKind kind;

    IncidentType(String label, IncidentKind kind) {
        this.label = label;
        this.kind = kind;
    }

    public String getLabel() {
        return label;
    }

    public IncidentKind getKind() {
        return kind;
    }
}
