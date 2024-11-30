package tracking.consultant.backend.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillLevel;
import tracking.consultant.backend.infrastructure.mappers.ConsultantMapper;
import tracking.consultant.backend.infrastructure.mappers.SkillMapper;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsultantRepositoryAdapter implements ConsultantRepositoryPort {
    private final ConsultantRepository repository;
    private final ConsultantMapper mapper;
    private final SkillMapper skillMapper;

    @Override
    public Consultant save(Consultant consultant) {
        return mapper.toModel(repository.save(mapper.toEntity(consultant)));
    }

    @Override
    public Optional<Consultant> findById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public List<Consultant> findAll() {
        return repository.findAll().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<Consultant> findBySkill(Skill skill) {
        return repository.findBySkills_Skill_Id(skill.getId()).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultant> findBySkills(Skill skill) {
        return findBySkill(skill);
    }

    @Override
    public List<Consultant> findByProject(Project project) {
        return repository.findByCurrentProjectId(project.getId()).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultant> findAvailableConsultants() {
        return repository.findAvailableConsultants().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultant> findBySkillLevel(Skill skill, int minimumLevel) {
        return repository.findBySkillAndMinimumLevel(skillMapper.toEntity(skill), minimumLevel).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultant> findBySkillAndLevel(Skill skill, SkillLevel level) {
        return repository.findBySkillAndLevel(skillMapper.toEntity(skill), level).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Consultant> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toModel);
    }

    @Override
    public void delete(Consultant consultant) {
        repository.delete(mapper.toEntity(consultant));
    }
}
