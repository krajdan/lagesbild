package se.lagesbild.incident;

/**
 * Rapporterande aktor. EMBRACE bygger pa samverkan mellan flera aktorer
 * som delar samma lagesbild: kommun, polis, bostadsbolag m.fl.
 */
public enum Actor {
    KOMMUN("Kommun"),
    POLIS("Polis"),
    BOSTADSBOLAG("Bostadsbolag"),
    VAKTBOLAG("Vaktbolag"),
    INVANARE("Invanare");

    private final String label;

    Actor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
