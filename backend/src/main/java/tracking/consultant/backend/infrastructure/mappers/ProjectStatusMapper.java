package tracking.consultant.backend.infrastructure.mappers;

import tracking.consultant.backend.domain.model.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class ProjectStatusMapper {
    
    public tracking.consultant.backend.infrastructure.enums.ProjectStatus toInfrastructure(ProjectStatus status) {
        if (status == null) return null;
        
        return switch (status) {
            case NOT_STARTED -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.PENDING;
            case PLANNED -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.PENDING;
            case IN_PROGRESS -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.IN_PROGRESS;
            case ON_HOLD -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.IN_PROGRESS;
            case COMPLETED -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.COMPLETED;
            case CANCELLED -> tracking.consultant.backend.infrastructure.enums.ProjectStatus.CANCELLED;
        };
    }
    
    public ProjectStatus toDomain(tracking.consultant.backend.infrastructure.enums.ProjectStatus status) {
        if (status == null) return null;
        
        return switch (status) {
            case PENDING -> ProjectStatus.PLANNED;
            case IN_PROGRESS -> ProjectStatus.IN_PROGRESS;
            case COMPLETED -> ProjectStatus.COMPLETED;
            case CANCELLED -> ProjectStatus.CANCELLED;
        };
    }
}
