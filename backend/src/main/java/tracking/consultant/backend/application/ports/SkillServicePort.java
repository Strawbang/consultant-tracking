package tracking.consultant.backend.application.ports;

import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillCategory;

import java.util.List;
import java.util.Optional;

public interface SkillServicePort {
    Skill createSkill(Skill skill);
    Optional<Skill> getSkillById(Long id);
    List<Skill> getAllSkills();
    List<Skill> getSkillsByCategory(SkillCategory category);
    List<Skill> getSkillsByConsultant(Long consultantId);
    List<Skill> getSkillsByProject(Long projectId);
    Skill updateSkill(Long id, Skill skill);
    void deleteSkill(Long id);
    void assignSkillToCategory(Long skillId, SkillCategory category);
    void updateSkillCategory(Long skillId, SkillCategory newCategory);
}
