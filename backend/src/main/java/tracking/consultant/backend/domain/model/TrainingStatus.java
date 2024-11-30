package tracking.consultant.backend.domain.model;

public enum TrainingStatus {
    PLANNED("Planifié"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé");

    private final String displayName;

    TrainingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEditable() {
        return this == PLANNED;
    }

    public boolean canTransitionTo(TrainingStatus newStatus) {
        if (this.isTerminal()) {
            return false;
        }
        
        switch (this) {
            case PLANNED:
                return newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS:
                return newStatus == COMPLETED || newStatus == CANCELLED;
            default:
                return false;
        }
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean allowsRegistration() {
        return this == PLANNED || this == IN_PROGRESS;
    }

    public boolean allowsUnregistration() {
        return this == PLANNED || this == IN_PROGRESS;
    }
}
