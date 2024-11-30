package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Training;
import tracking.consultant.backend.infrastructure.entities.TrainingEntity;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {ConsultantMapper.class, SkillMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface TrainingMapper {

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "completedParticipants", ignore = true)
    @Mapping(target = "skillsTaught", ignore = true)
    Training toModel(TrainingEntity entity);

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "completedParticipants", ignore = true)
    @Mapping(target = "skillsTaught", ignore = true)
    TrainingEntity toEntity(Training model);

    default List<Training> toModelList(List<TrainingEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toModel).toList();
    }

    default List<TrainingEntity> toEntityList(List<Training> models) {
        if (models == null) return null;
        return models.stream().map(this::toEntity).toList();
    }

    default Set<Training> toModelSet(Set<TrainingEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toModel).collect(java.util.stream.Collectors.toSet());
    }

    default Set<TrainingEntity> toEntitySet(Set<Training> models) {
        if (models == null) return null;
        return models.stream().map(this::toEntity).collect(java.util.stream.Collectors.toSet());
    }
}
