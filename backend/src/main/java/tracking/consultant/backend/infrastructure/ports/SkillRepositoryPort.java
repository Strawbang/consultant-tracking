package tracking.consultant.backend.infrastructure.ports;

import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillCategory;

import java.util.List;
import java.util.Optional;

public interface SkillRepositoryPort {
    Skill save(Skill skill);
    Optional<Skill> findById(Long id);
    List<Skill> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Skill> findByCategory(SkillCategory category);
    List<Skill> findByConsultantId(Long consultantId);
    List<Skill> findByProjectId(Long projectId);
}
