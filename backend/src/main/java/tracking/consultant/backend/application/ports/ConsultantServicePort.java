package tracking.consultant.backend.application.ports;

import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.SkillLevel;

import java.util.List;
import java.util.Optional;

public interface ConsultantServicePort {
    Consultant createConsultant(Consultant consultant);
    Optional<Consultant> getConsultantById(Long id);
    List<Consultant> getAllConsultants();
    List<Consultant> getConsultantsBySkill(Skill skill);
    List<Consultant> getConsultantsByProject(Project project);
    List<Consultant> getAvailableConsultants();
    List<Consultant> getConsultantsBySkillLevel(Skill skill, int minimumLevel);
    Consultant updateConsultant(Long id, Consultant consultant);
    void deleteConsultant(Long id);
    void addSkillToConsultant(Long consultantId, Long skillId, SkillLevel level);
    void removeSkillFromConsultant(Long consultantId, Long skillId);
    void updateConsultantSkillLevel(Long consultantId, Long skillId, SkillLevel newLevel);
}
