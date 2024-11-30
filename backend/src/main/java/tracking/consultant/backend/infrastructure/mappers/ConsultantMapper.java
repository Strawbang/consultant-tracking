package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillLevel;
import tracking.consultant.backend.infrastructure.entities.ConsultantEntity;
import tracking.consultant.backend.infrastructure.entities.ConsultantSkillEntity;

import java.util.*;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class, SkillMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface ConsultantMapper {

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "evaluations", ignore = true)
    @Mapping(target = "missions", ignore = true)
    Consultant toModel(ConsultantEntity entity);

    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "currentProject", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "evaluations", ignore = true)
    @Mapping(target = "missions", ignore = true)
    ConsultantEntity toEntity(Consultant model);

    default List<Consultant> toModelList(List<ConsultantEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        List<Consultant> list = new ArrayList<>(entities.size());
        for (ConsultantEntity entity : entities) {
            list.add(toModel(entity));
        }
        return list;
    }

    default List<ConsultantEntity> toEntityList(List<Consultant> models) {
        if (models == null) {
            return Collections.emptyList();
        }
        List<ConsultantEntity> list = new ArrayList<>(models.size());
        for (Consultant model : models) {
            list.add(toEntity(model));
        }
        return list;
    }

    default Set<Consultant> toModelSet(Set<ConsultantEntity> entities) {
        if (entities == null) {
            return Collections.emptySet();
        }
        Set<Consultant> set = new HashSet<>(entities.size());
        for (ConsultantEntity entity : entities) {
            set.add(toModel(entity));
        }
        return set;
    }

    default Set<ConsultantEntity> toEntitySet(Set<Consultant> models) {
        if (models == null) {
            return Collections.emptySet();
        }
        Set<ConsultantEntity> set = new HashSet<>(models.size());
        for (Consultant model : models) {
            set.add(toEntity(model));
        }
        return set;
    }

    default SkillLevel map(int level) {
        return SkillLevel.fromValue(level);
    }

    @AfterMapping
    default void mapSkillsAfterMapping(@MappingTarget Consultant consultant, ConsultantEntity entity, @Context SkillMapper skillMapper) {
        if (entity.getSkills() != null) {
            Map<Skill, SkillLevel> skillMap = new HashMap<>();
            for (ConsultantSkillEntity skillEntity : entity.getSkills()) {
                skillMap.put(skillMapper.toDomain(skillEntity.getSkill()), map(skillEntity.getSkillLevel()));
            }
            consultant.setSkills(skillMap);
        }
    }

    @AfterMapping
    default void mapProjectsAfterMapping(@MappingTarget Consultant consultant, ConsultantEntity entity, @Context ProjectMapper projectMapper) {
        if (entity.getCurrentProject() != null) {
            Set<Project> projects = new HashSet<>();
            projects.add(projectMapper.toModel(entity.getCurrentProject()));
            consultant.setProjects(projects);
        }
    }
}
