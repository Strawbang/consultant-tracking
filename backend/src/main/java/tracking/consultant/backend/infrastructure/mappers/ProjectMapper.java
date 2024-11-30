package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.infrastructure.entities.ProjectEntity;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {ProjectStatusMapper.class, ConsultantMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ProjectMapper {

    @Mapping(target = "consultants", ignore = true)
    @Mapping(target = "requiredSkills", ignore = true)
    Project toModel(ProjectEntity entity);

    @Mapping(target = "consultants", ignore = true)
    @Mapping(target = "requiredSkills", ignore = true)
    ProjectEntity toEntity(Project model);

    List<Project> toModelList(List<ProjectEntity> entities);
    List<ProjectEntity> toEntityList(List<Project> models);
    Set<Project> toModelSet(Set<ProjectEntity> entities);
    Set<ProjectEntity> toEntitySet(Set<Project> models);

    @ValueMappings({
        @ValueMapping(source = "NOT_STARTED", target = "PLANNED"),
        @ValueMapping(source = "PLANNED", target = "PLANNED"),
        @ValueMapping(source = "IN_PROGRESS", target = "IN_PROGRESS"),
        @ValueMapping(source = "ON_HOLD", target = "ON_HOLD"),
        @ValueMapping(source = "COMPLETED", target = "COMPLETED"),
        @ValueMapping(source = "CANCELLED", target = "CANCELLED")
    })
    ProjectEntity.ProjectStatus map(ProjectStatus status);

    @ValueMappings({
        @ValueMapping(source = "PLANNED", target = "PLANNED"),
        @ValueMapping(source = "IN_PROGRESS", target = "IN_PROGRESS"),
        @ValueMapping(source = "ON_HOLD", target = "ON_HOLD"),
        @ValueMapping(source = "COMPLETED", target = "COMPLETED"),
        @ValueMapping(source = "CANCELLED", target = "CANCELLED")
    })
    ProjectStatus map(ProjectEntity.ProjectStatus status);
}
