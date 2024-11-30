package tracking.consultant.backend.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillCategory;
import tracking.consultant.backend.infrastructure.mappers.SkillMapper;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillRepositoryAdapter implements SkillRepositoryPort {
    private final SkillRepository repository;
    private final SkillMapper mapper;

    @Override
    public Skill save(Skill skill) {
        return mapper.toDomain(repository.save(mapper.toEntity(skill)));
    }

    @Override
    public Optional<Skill> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Skill> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Skill> findByCategory(SkillCategory category) {
        return repository.findByCategory(category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Skill> findByConsultantId(Long consultantId) {
        return repository.findByConsultantId(consultantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Skill> findByProjectId(Long projectId) {
        return repository.findByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Skill> findBaseSkills() {
        return repository.findBaseSkills().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Skill> findByPrerequisite(Skill prerequisite) {
        return repository.findByPrerequisite(mapper.toEntity(prerequisite)).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public void delete(Skill skill) {
        repository.delete(mapper.toEntity(skill));
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
