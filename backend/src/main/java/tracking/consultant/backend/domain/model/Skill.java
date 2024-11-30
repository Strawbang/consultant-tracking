package tracking.consultant.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private Long id;
    private String name;
    private String description;
    private SkillLevel level;
    private SkillCategory category;
    private Set<Skill> prerequisites = new HashSet<>();

    public Set<Skill> getPrerequisites() {
        return prerequisites;
    }

    public void addPrerequisite(Skill skill) {
        prerequisites.add(skill);
    }

    public void removePrerequisite(Skill skill) {
        prerequisites.remove(skill);
    }

    public boolean isAdvancedOrHigher() {
        return level == SkillLevel.ADVANCED || level == SkillLevel.EXPERT;
    }

    public boolean isInCategory(SkillCategory category) {
        return this.category == category;
    }
}
