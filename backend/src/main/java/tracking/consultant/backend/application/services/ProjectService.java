package tracking.consultant.backend.application.services;

import tracking.consultant.backend.application.ports.ProjectServicePort;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillLevel;
import tracking.consultant.backend.infrastructure.ports.ProjectRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService implements ProjectServicePort {
    
    private final ProjectRepositoryPort projectRepository;
    private final ConsultantRepositoryPort consultantRepository;
    private final SkillRepositoryPort skillRepository;

    private void validateProjectDates(Project project) {
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getStartDate().isAfter(project.getEndDate())) {
                throw new IllegalStateException("La date de début du projet ne peut pas être après la date de fin");
            }
        }
    }

    private void validateProjectAssignment(Project project, Consultant consultant) {
        if (consultant.getProjects().size() >= consultant.getMaxSimultaneousProjects()) {
            throw new IllegalStateException("Le consultant a atteint son nombre maximum de projets simultanés");
        }

        boolean hasRequiredSkills = project.getRequiredSkills().stream()
                .allMatch(requiredSkill -> 
                    consultant.getSkills().stream()
                        .anyMatch(consultantSkill -> 
                            consultantSkill.getName().equals(requiredSkill.getName()) &&
                            consultantSkill.getLevel().ordinal() >= requiredSkill.getMinimumLevel()
                        )
                );

        if (!hasRequiredSkills) {
            throw new IllegalStateException("Le consultant ne possède pas toutes les compétences requises pour ce projet");
        }
    }

    private boolean isConsultantAvailableForProject(Consultant consultant, Project project) {
        return consultant.getProjects().stream()
                .noneMatch(existingProject -> 
                    existingProject.getStartDate().isBefore(project.getEndDate()) &&
                    existingProject.getEndDate().isAfter(project.getStartDate())
                );
    }

    @Override
    public Project createProject(Project project) {
        validateProjectDates(project);
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> getAllProjects() {
        try {
            System.out.println("Service - Récupération de tous les projets");
            List<Project> projects = projectRepository.findAll();
            System.out.println("Service - Nombre de projets trouvés : " + projects.size());
            return projects;
        } catch (Exception e) {
            System.err.println("Service - Erreur lors de la récupération des projets : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    @Override
    public List<Project> getProjectsByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    public List<Project> getProjectsByConsultant(Consultant consultant) {
        return projectRepository.findByConsultant(consultant);
    }

    public List<Project> getProjectsByRequiredSkill(Skill skill) {
        return projectRepository.findByRequiredSkill(skill);
    }

    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public Set<Consultant> getProjectConsultants(Long projectId) {
        return projectRepository.findProjectConsultants(projectId);
    }

    @Override
    public Set<Skill> getProjectRequiredSkills(Long projectId) {
        return projectRepository.findProjectRequiredSkills(projectId);
    }

    @Override
    public Project updateProject(Long id, Project project) {
        Project existingProject = projectRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + id));
        
        existingProject.setName(project.getName());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setEndDate(project.getEndDate());
        existingProject.setStatus(project.getStatus());
        existingProject.setDescription(project.getDescription());
        
        validateProjectDates(existingProject);
        return projectRepository.save(existingProject);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Projet non trouvé avec l'id: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignConsultantToProject(Long projectId, Long consultantId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        validateProjectAssignment(project, consultant);
        
        if (!isConsultantAvailableForProject(consultant, project)) {
            throw new IllegalStateException("Le consultant n'est pas disponible pour ce projet");
        }

        project.getConsultants().add(consultant);
        consultant.getProjects().add(project);
        
        projectRepository.save(project);
        consultantRepository.save(consultant);
    }

    @Override
    @Transactional
    public void removeConsultantFromProject(Long projectId, Long consultantId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        project.getConsultants().remove(consultant);
        consultant.getProjects().remove(project);
        
        projectRepository.save(project);
        consultantRepository.save(consultant);
    }

    public void addRequiredSkillToProject(Long projectId, Long skillId, int minimumLevel) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));

        if (project.getRequiredSkills().stream()
                .anyMatch(existingSkill -> existingSkill.getName().equals(skill.getName()))) {
            throw new IllegalStateException("Cette compétence est déjà requise pour ce projet");
        }
        
        project.addRequiredSkill(skill, minimumLevel);
        projectRepository.save(project);
    }

    public void removeRequiredSkillFromProject(Long projectId, Long skillId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        
        project.getRequiredSkills().removeIf(requiredSkill -> requiredSkill.getName().equals(skill.getName()));
        projectRepository.save(project);
    }

    @Override
    public List<Project> getProjectsByConsultant(Long consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        return projectRepository.findByConsultant(consultant);
    }

    @Override
    public List<Project> getProjectsByRequiredSkill(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        return projectRepository.findByRequiredSkill(skill);
    }

    @Override
    public boolean isConsultantQualifiedForProject(Long consultantId, Long projectId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));

        // Check if consultant has all required skills at minimum level
        return project.getRequiredSkills().stream()
            .allMatch(requiredSkill -> {
                SkillLevel consultantLevel = consultant.getSkillLevel(requiredSkill);
                return consultantLevel != null && consultantLevel.getValue() >= requiredSkill.getMinimumLevel();
            });
    }

    @Override
    public double getProjectProgress(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        if (project.getStatus() == ProjectStatus.NOT_STARTED) {
            return 0.0;
        }
        if (project.getStatus() == ProjectStatus.COMPLETED) {
            return 100.0;
        }
        
        // Calculate progress based on time elapsed
        LocalDate now = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(project.getStartDate(), project.getEndDate());
        long daysElapsed = ChronoUnit.DAYS.between(project.getStartDate(), now);
        
        return Math.min(100.0, Math.max(0.0, (daysElapsed * 100.0) / totalDays));
    }

    @Override
    public void updateProjectStatus(Long projectId, ProjectStatus newStatus) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id: " + projectId));
        
        // Add validation logic for status transitions
        if (project.getStatus() == ProjectStatus.COMPLETED && newStatus != ProjectStatus.COMPLETED) {
            throw new IllegalStateException("Un projet terminé ne peut pas changer de statut");
        }
        
        project.setStatus(newStatus);
        projectRepository.save(project);
    }
}
