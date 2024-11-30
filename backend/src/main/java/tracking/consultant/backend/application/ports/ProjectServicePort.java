package tracking.consultant.backend.application.ports;

import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProjectServicePort {
    Project createProject(Project project);
    Optional<Project> getProjectById(Long id);
    List<Project> getAllProjects();
    List<Project> getActiveProjects();
    List<Project> getProjectsByStatus(ProjectStatus status);
    List<Project> getProjectsByConsultant(Long consultantId);
    List<Project> getProjectsByRequiredSkill(Long skillId);
    List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
    void assignConsultantToProject(Long projectId, Long consultantId);
    void removeConsultantFromProject(Long projectId, Long consultantId);
    void addRequiredSkillToProject(Long projectId, Long skillId, int minimumLevel);
    void removeRequiredSkillFromProject(Long projectId, Long skillId);
    Set<Consultant> getProjectConsultants(Long projectId);
    Set<Skill> getProjectRequiredSkills(Long projectId);
    boolean isConsultantQualifiedForProject(Long consultantId, Long projectId);
    double getProjectProgress(Long projectId);
    void updateProjectStatus(Long projectId, ProjectStatus newStatus);
}
