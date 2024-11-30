package tracking.consultant.backend.domain.model;

public enum EvaluationStatus {
    DRAFT("Brouillon"),
    SCHEDULED("Planifiée"),
    IN_PROGRESS("En cours"),
    COMPLETED("Terminée"),
    REJECTED("Rejetée"),
    CANCELLED("Annulée"),
    ARCHIVED("Archivée");

    private final String frenchLabel;

    EvaluationStatus(String frenchLabel) {
        this.frenchLabel = frenchLabel;
    }

    public String getFrenchLabel() {
        return frenchLabel;
    }

    public boolean isEditable() {
        return this == DRAFT || this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == ARCHIVED || this == REJECTED;
    }

    public boolean isPending() {
        return this == SCHEDULED || this == IN_PROGRESS;
    }

    public boolean canTransitionTo(EvaluationStatus newStatus) {
        switch (this) {
            case DRAFT:
                return newStatus == SCHEDULED || newStatus == CANCELLED;
            case SCHEDULED:
                return newStatus == IN_PROGRESS || newStatus == CANCELLED;
            case IN_PROGRESS:
                return newStatus == COMPLETED || newStatus == REJECTED || newStatus == CANCELLED;
            case COMPLETED:
                return newStatus == ARCHIVED;
            case REJECTED:
                return newStatus == DRAFT || newStatus == ARCHIVED;
            case CANCELLED:
                return newStatus == ARCHIVED;
            case ARCHIVED:
                return false;
            default:
                return false;
        }
    }

    public boolean requiresValidation() {
        return this == IN_PROGRESS || this == COMPLETED;
    }

    public boolean canBeReopened() {
        return this == REJECTED;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }
}
