package tracking.consultant.backend.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.infrastructure.mappers.ConsultantMapper;
import tracking.consultant.backend.infrastructure.mappers.ProjectMapper;
import tracking.consultant.backend.infrastructure.mappers.ProjectStatusMapper;
import tracking.consultant.backend.infrastructure.mappers.SkillMapper;
import tracking.consultant.backend.infrastructure.ports.ProjectRepositoryPort;
import tracking.consultant.backend.infrastructure.entities.ConsultantEntity;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter implements ProjectRepositoryPort {
    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final ConsultantMapper consultantMapper;
    private final SkillMapper skillMapper;
    private final ProjectStatusMapper statusMapper;

    @Override
    public Project save(Project project) {
        return mapper.toModel(repository.save(mapper.toEntity(project)));
    }

    @Override
    public Optional<Project> findById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public List<Project> findAll() {
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

    public List<Project> findByRequiredSkillId(Long skillId) {
        return repository.findByRequiredSkillId(skillId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Consultant> findProjectConsultants(Long projectId) {
        return repository.findProjectConsultants(projectId).stream()
                .map(consultantMapper::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Project> findActiveProjects() {
        return repository.findActiveProjects().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Project> findProjectsNeedingConsultants() {
        return repository.findProjectsNeedingConsultants().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Project> findByConsultantId(Long consultantId) {
        return repository.findByConsultants_Id(consultantId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    public void delete(Project project) {
        repository.delete(mapper.toEntity(project));
    }

    public List<Project> findByStatus(ProjectStatus status) {
        return repository.findByStatus(statusMapper.toInfrastructure(status)).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }


    public List<Project> findByConsultant(Consultant consultant) {
        ConsultantEntity consultantEntity = consultantMapper.toEntity(consultant);
        return repository.findByConsultant(consultantEntity).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByRequiredSkill(Skill skill) {
        SkillEntity skillEntity = skillMapper.toEntity(skill);
        return repository.findByRequiredSkill(skillEntity).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Skill> findProjectRequiredSkills(Long projectId) {
        return repository.findProjectRequiredSkills(projectId).stream()
                .map(skillMapper::toDomain)
                .collect(Collectors.toSet());
    }
}
