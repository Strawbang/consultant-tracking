package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.*;
import tracking.consultant.backend.domain.model.Mission;
import tracking.consultant.backend.infrastructure.entities.MissionEntity;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        uses = {ConsultantMapper.class, SkillMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface MissionMapper {

    @Mapping(target = "consultant", ignore = true)
    @Mapping(target = "requiredSkills", ignore = true)
    Mission toModel(MissionEntity entity);

    @Mapping(target = "consultant", ignore = true)
    @Mapping(target = "requiredSkills", ignore = true)
    MissionEntity toEntity(Mission model);

    List<Mission> toModelList(List<MissionEntity> entities);
    List<MissionEntity> toEntityList(List<Mission> models);
    Set<Mission> toModelSet(Set<MissionEntity> entities);
    Set<MissionEntity> toEntitySet(Set<Mission> models);
}
