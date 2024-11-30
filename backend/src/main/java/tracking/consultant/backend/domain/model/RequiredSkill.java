package tracking.consultant.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RequiredSkill extends Skill {
    private int minimumLevel;

    public RequiredSkill(Skill skill, int minimumLevel) {
        super(skill.getId(), skill.getName(), skill.getDescription(), 
              skill.getLevel(), skill.getCategory(), skill.getPrerequisites());
        this.minimumLevel = minimumLevel;
    }
}
