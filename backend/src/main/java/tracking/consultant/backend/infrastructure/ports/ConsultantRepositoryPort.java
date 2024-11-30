package tracking.consultant.backend.infrastructure.ports;

import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.SkillLevel;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepositoryPort {
    Consultant save(Consultant consultant);
    Optional<Consultant> findById(Long id);
    List<Consultant> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Consultant> findBySkill(Skill skill);
    List<Consultant> findBySkills(Skill skill);
    List<Consultant> findByProject(Project project);
    List<Consultant> findAvailableConsultants();
    List<Consultant> findBySkillLevel(Skill skill, int minimumLevel);
    List<Consultant> findBySkillAndLevel(Skill skill, SkillLevel level);
    Optional<Consultant> findByEmail(String email);
    void delete(Consultant consultant);
}
