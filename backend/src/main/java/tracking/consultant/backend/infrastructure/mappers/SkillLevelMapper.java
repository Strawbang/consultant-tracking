package tracking.consultant.backend.infrastructure.mappers;

import org.mapstruct.Named;
import tracking.consultant.backend.domain.model.SkillLevel;

public class SkillLevelMapper {
    
    @Named("toSkillLevel")
    public static SkillLevel toSkillLevel(Integer value) {
        if (value == null) return null;
        return SkillLevel.values()[value - 1];
    }

    @Named("fromSkillLevel")
    public static Integer fromSkillLevel(SkillLevel level) {
        return level == null ? null : level.getValue();
    }
}
