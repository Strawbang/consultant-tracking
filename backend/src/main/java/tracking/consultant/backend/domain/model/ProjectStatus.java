package tracking.consultant.backend.domain.model;

public enum ProjectStatus {
    NOT_STARTED("Non démarré"),
    PLANNED("Planifié"),
    IN_PROGRESS("En cours"),
    ON_HOLD("En pause"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
