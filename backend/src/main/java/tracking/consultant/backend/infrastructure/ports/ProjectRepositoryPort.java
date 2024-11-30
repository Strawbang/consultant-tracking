package tracking.consultant.backend.infrastructure.ports;

import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectRepositoryPort {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Project> findActiveProjects();
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByConsultant(Consultant consultant);
    List<Project> findByRequiredSkill(Skill skill);
    List<Project> findByDateRange(LocalDate startDate, LocalDate endDate);
    Set<Consultant> findProjectConsultants(Long projectId);
    Set<Skill> findProjectRequiredSkills(Long projectId);
}
