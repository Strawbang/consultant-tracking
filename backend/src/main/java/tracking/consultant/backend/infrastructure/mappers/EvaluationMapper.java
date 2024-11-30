package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Evaluation;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.infrastructure.entities.EvaluationEntity;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {ConsultantMapper.class, ProjectMapper.class, SkillMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface EvaluationMapper {

    @Mapping(target = "skillScores", ignore = true)
    Evaluation toDomain(EvaluationEntity entity);

    @Mapping(target = "skillScores", ignore = true)
    @Mapping(target = "version", ignore = true)
    EvaluationEntity toEntity(Evaluation model);

    default List<Evaluation> toDomainList(List<EvaluationEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).toList();
    }

    default List<EvaluationEntity> toEntityList(List<Evaluation> models) {
        if (models == null) return null;
        return models.stream().map(this::toEntity).toList();
    }

    @AfterMapping
    default void mapSkillScores(@MappingTarget Evaluation evaluation, EvaluationEntity entity, @Context SkillMapper skillMapper) {
        if (entity.getSkillScores() != null) {
            Map<Skill, Integer> skillScores = entity.getSkillScores().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> skillMapper.toDomain(entry.getKey()),
                    Map.Entry::getValue
                ));
            evaluation.setSkillScores(skillScores);
        }
    }

    @AfterMapping
    default void mapSkillScores(@MappingTarget EvaluationEntity entity, Evaluation evaluation, @Context SkillMapper skillMapper) {
        if (evaluation.getSkillScores() != null) {
            Map<SkillEntity, Integer> skillScores = evaluation.getSkillScores().entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> skillMapper.toEntity(entry.getKey()),
                    Map.Entry::getValue
                ));
            entity.setSkillScores(skillScores);
        }
    }
}
