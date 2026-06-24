package se.lagesbild.intervention;

public enum InterventionStatus {
    PLANERAD("Planerad"),
    PAGAR("Pagar"),
    AVSLUTAD("Avslutad");

    private final String label;

    InterventionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
