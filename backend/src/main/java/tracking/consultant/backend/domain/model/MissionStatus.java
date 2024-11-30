package tracking.consultant.backend.domain.model;

public enum MissionStatus {
    PLANNED("Planifiée"),
    IN_PROGRESS("En cours"),
    ON_HOLD("En pause"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée");

    private final String frenchLabel;

    MissionStatus(String frenchLabel) {
        this.frenchLabel = frenchLabel;
    }

    public String getFrenchLabel() {
        return frenchLabel;
    }

    public boolean isActive() {
        return this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(MissionStatus newStatus) {
        if (this.isTerminal()) {
            return false;
        }
        
        switch (this) {
            case PLANNED:
                return newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS:
                return newStatus == ON_HOLD || newStatus == COMPLETED || newStatus == CANCELLED;
            case ON_HOLD:
                return newStatus == IN_PROGRESS || newStatus == CANCELLED;
            default:
                return false;
        }
    }
}
