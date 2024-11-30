package tracking.consultant.backend.application.services;

import tracking.consultant.backend.application.ports.ProjectServicePort;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.infrastructure.ports.ProjectRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectApplicationService implements ProjectServicePort {
    
    private final ProjectRepositoryPort projectRepository;
    private final ConsultantRepositoryPort consultantRepository;
    private final SkillRepositoryPort skillRepository;

    public Project createProject(Project project) {
        project.validateDates();
        return projectRepository.save(project);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    public List<Project> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getProjectsByConsultant(Long consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        return projectRepository.findByConsultant(consultant);
    }

    public List<Project> getProjectsByRequiredSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        return projectRepository.findByRequiredSkill(skill);
    }

    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByDateRange(startDate, endDate);
    }

    public Set<Consultant> getProjectConsultants(Long projectId) {
        return projectRepository.findProjectConsultants(projectId);
    }

    public Set<Skill> getProjectRequiredSkills(Long projectId) {
        return projectRepository.findProjectRequiredSkills(projectId);
    }

    public Project updateProject(Long id, Project project) {
        Project existingProject = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + id));
        
        // Copy properties from incoming project to existing project
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setEndDate(project.getEndDate());
        existingProject.setStatus(project.getStatus());
        
        existingProject.validateDates();
        return projectRepository.save(existingProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Projet non trouvé avec l'id: " + id);
        }
        projectRepository.deleteById(id);
    }

    public void assignConsultantToProject(Long projectId, Long consultantId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        project.addConsultant(consultant);
        projectRepository.save(project);
    }

    public void removeConsultantFromProject(Long projectId, Long consultantId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        project.removeConsultant(consultant);
        projectRepository.save(project);
    }


    public void addRequiredSkillToProject(Long projectId, Long skillId, int minimumLevel) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        
        project.addRequiredSkill(skill, minimumLevel);
        projectRepository.save(project);
    }

    public void removeRequiredSkillFromProject(Long projectId, Long skillId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        
        project.removeRequiredSkill(skill);
        projectRepository.save(project);
    }

    public boolean isConsultantQualifiedForProject(Long consultantId, Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        return project.isConsultantQualified(consultant);
    }

    @Override
    public double getProjectProgress(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        return project.calculateProgress();
    }

    @Override
    @Transactional
    public void updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        project.setStatus(newStatus);
        projectRepository.save(project);
    }
}
