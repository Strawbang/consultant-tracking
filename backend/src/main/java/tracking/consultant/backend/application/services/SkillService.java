package tracking.consultant.backend.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tracking.consultant.backend.application.ports.SkillServicePort;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillCategory;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService implements SkillServicePort {
    private final SkillRepositoryPort skillRepository;

    @Override
    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Optional<Skill> getSkillById(Long id) {
        return skillRepository.findById(id);
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public List<Skill> getSkillsByCategory(SkillCategory category) {
        return skillRepository.findByCategory(category);
    }

    @Override
    public List<Skill> getSkillsByConsultant(Long consultantId) {
        return skillRepository.findByConsultantId(consultantId);
    }

    @Override
    public List<Skill> getSkillsByProject(Long projectId) {
        return skillRepository.findByProjectId(projectId);
    }

    @Override
    public Skill updateSkill(Long id, Skill skill) {
        if (!skillRepository.existsById(id)) {
            throw new IllegalArgumentException("Skill not found with id: " + id);
        }
        skill.setId(id);
        return skillRepository.save(skill);
    }

    @Override
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    public void assignSkillToCategory(Long skillId, SkillCategory category) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + skillId));
        skill.setCategory(category);
        skillRepository.save(skill);
    }

    @Override
    public void updateSkillCategory(Long skillId, SkillCategory newCategory) {
        assignSkillToCategory(skillId, newCategory);
    }
}
