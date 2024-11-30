package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillLevel;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface SkillMapper {

    Skill toDomain(SkillEntity entity);
    SkillEntity toEntity(Skill model);

    default SkillLevel map(Integer value) {
        if (value == null) return null;
        return switch (value) {
            case 1 -> SkillLevel.BEGINNER;
            case 2 -> SkillLevel.INTERMEDIATE;
            case 3 -> SkillLevel.ADVANCED;
            case 4 -> SkillLevel.EXPERT;
            default -> SkillLevel.BEGINNER;
        };
    }

    default Integer map(SkillLevel level) {
        if (level == null) return null;
        return switch (level) {
            case BEGINNER -> 1;
            case INTERMEDIATE -> 2;
            case ADVANCED -> 3;
            case EXPERT -> 4;
        };
    }

    default List<Skill> toDomainList(List<SkillEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).toList();
    }

    default List<SkillEntity> toEntityList(List<Skill> models) {
        if (models == null) return null;
        return models.stream().map(this::toEntity).toList();
    }

    default Set<Skill> toDomainSet(Set<SkillEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(java.util.stream.Collectors.toSet());
    }

    default Set<SkillEntity> toEntitySet(Set<Skill> models) {
        if (models == null) return null;
        return models.stream().map(this::toEntity).collect(java.util.stream.Collectors.toSet());
    }
}
