package tracking.consultant.backend.domain.model;

public enum EvaluationType {
    ANNUAL("Évaluation annuelle"),
    MONTHLY("Évaluation mensuelle"),
    QUARTERLY("Évaluation trimestrielle"),
    PROJECT("Évaluation de projet"),
    SKILLS("Évaluation des compétences"),
    PERFORMANCE("Évaluation de performance");

    private final String displayName;

    EvaluationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
