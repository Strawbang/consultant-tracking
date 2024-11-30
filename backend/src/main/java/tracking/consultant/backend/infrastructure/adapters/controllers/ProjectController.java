package tracking.consultant.backend.infrastructure.adapters.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracking.consultant.backend.application.ports.ProjectServicePort;
import tracking.consultant.backend.domain.model.Project;
import tracking.consultant.backend.domain.model.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "API pour la gestion des projets")
public class ProjectController {

    private final ProjectServicePort projectService;

    @PostMapping
    @Operation(summary = "Créer un projet")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un projet par son ID")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les projets")
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            System.out.println("Projets trouvés : " + projects.size());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des projets : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Obtenir les projets actifs")
    public ResponseEntity<List<Project>> getActiveProjects() {
        return ResponseEntity.ok(projectService.getActiveProjects());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir les projets par statut")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable ProjectStatus status) {
        return ResponseEntity.ok(projectService.getProjectsByStatus(status));
    }

    @GetMapping("/consultant/{consultantId}")
    @Operation(summary = "Obtenir les projets d'un consultant")
    public ResponseEntity<List<Project>> getProjectsByConsultant(@PathVariable Long consultantId) {
        return ResponseEntity.ok(projectService.getProjectsByConsultant(consultantId));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtenir les projets dans une plage de dates")
    public ResponseEntity<List<Project>> getProjectsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(projectService.getProjectsByDateRange(startDate, endDate));
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Obtenir les projets nécessitant une compétence spécifique")
    public ResponseEntity<List<Project>> getProjectsByRequiredSkill(@PathVariable Long skillId) {
        return ResponseEntity.ok(projectService.getProjectsByRequiredSkill(skillId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un projet")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un projet")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/consultants/{consultantId}")
    @Operation(summary = "Assigner un consultant à un projet")
    public ResponseEntity<Void> assignConsultantToProject(
            @PathVariable Long projectId,
            @PathVariable Long consultantId) {
        projectService.assignConsultantToProject(projectId, consultantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/consultants/{consultantId}")
    @Operation(summary = "Retirer un consultant d'un projet")
    public ResponseEntity<Void> removeConsultantFromProject(
            @PathVariable Long projectId,
            @PathVariable Long consultantId) {
        projectService.removeConsultantFromProject(projectId, consultantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/skills/{skillId}")
    @Operation(summary = "Ajouter une compétence requise à un projet")
    public ResponseEntity<Void> addRequiredSkillToProject(
            @PathVariable Long projectId,
            @PathVariable Long skillId,
            @RequestParam int requiredLevel) {
        projectService.addRequiredSkillToProject(projectId, skillId, requiredLevel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/skills/{skillId}")
    @Operation(summary = "Retirer une compétence requise d'un projet")
    public ResponseEntity<Void> removeRequiredSkillFromProject(
            @PathVariable Long projectId,
            @PathVariable Long skillId) {
        projectService.removeRequiredSkillFromProject(projectId, skillId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{projectId}/status/{status}")
    @Operation(summary = "Mettre à jour le statut d'un projet")
    public ResponseEntity<Void> updateProjectStatus(
            @PathVariable Long projectId,
            @PathVariable ProjectStatus status) {
        projectService.updateProjectStatus(projectId, status);
        return ResponseEntity.ok().build();
    }
}
