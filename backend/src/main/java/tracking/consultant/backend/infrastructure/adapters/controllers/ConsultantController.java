package tracking.consultant.backend.infrastructure.adapters.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracking.consultant.backend.application.ports.ConsultantServicePort;
import tracking.consultant.backend.domain.model.Consultant;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillLevel;
import tracking.consultant.backend.domain.model.Project;

import java.util.List;

@RestController
@RequestMapping("/api/consultants")
@RequiredArgsConstructor
@Tag(name = "Consultant", description = "API pour la gestion des consultants")
public class ConsultantController {

    private final ConsultantServicePort consultantService;

    @PostMapping
    @Operation(summary = "Créer un consultant")
    public ResponseEntity<Consultant> createConsultant(@RequestBody Consultant consultant) {
        return ResponseEntity.ok(consultantService.createConsultant(consultant));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un consultant par son ID")
    public ResponseEntity<Consultant> getConsultantById(@PathVariable Long id) {
        return consultantService.getConsultantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les consultants")
    public ResponseEntity<List<Consultant>> getAllConsultants() {
        return ResponseEntity.ok(consultantService.getAllConsultants());
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Obtenir les consultants par compétence")
    public ResponseEntity<List<Consultant>> getConsultantsBySkill(@PathVariable Long skillId) {
        Skill skill = new Skill();
        skill.setId(skillId);
        return ResponseEntity.ok(consultantService.getConsultantsBySkill(skill));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Obtenir les consultants par projet")
    public ResponseEntity<List<Consultant>> getConsultantsByProject(@PathVariable Long projectId) {
        Project project = new Project();
        project.setId(projectId);
        return ResponseEntity.ok(consultantService.getConsultantsByProject(project));
    }

    @GetMapping("/available")
    @Operation(summary = "Obtenir les consultants disponibles")
    public ResponseEntity<List<Consultant>> getAvailableConsultants() {
        return ResponseEntity.ok(consultantService.getAvailableConsultants());
    }

    @GetMapping("/skill/{skillId}/level/{level}")
    @Operation(summary = "Obtenir les consultants par niveau de compétence")
    public ResponseEntity<List<Consultant>> getConsultantsBySkillLevel(
            @PathVariable Long skillId,
            @PathVariable int level) {
        Skill skill = new Skill();
        skill.setId(skillId);
        return ResponseEntity.ok(consultantService.getConsultantsBySkillLevel(skill, level));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un consultant")
    public ResponseEntity<Consultant> updateConsultant(
            @PathVariable Long id,
            @RequestBody Consultant consultant) {
        return ResponseEntity.ok(consultantService.updateConsultant(id, consultant));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un consultant")
    public ResponseEntity<Void> deleteConsultant(@PathVariable Long id) {
        consultantService.deleteConsultant(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{consultantId}/skills/{skillId}/level/{level}")
    @Operation(summary = "Ajouter une compétence à un consultant")
    public ResponseEntity<Void> addSkillToConsultant(
            @PathVariable Long consultantId,
            @PathVariable Long skillId,
            @PathVariable int level) {
        consultantService.addSkillToConsultant(consultantId, skillId, SkillLevel.values()[level - 1]);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{consultantId}/skills/{skillId}")
    @Operation(summary = "Supprimer une compétence d'un consultant")
    public ResponseEntity<Void> removeSkillFromConsultant(
            @PathVariable Long consultantId,
            @PathVariable Long skillId) {
        consultantService.removeSkillFromConsultant(consultantId, skillId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{consultantId}/skills/{skillId}/level/{newLevel}")
    @Operation(summary = "Mettre à jour le niveau de compétence d'un consultant")
    public ResponseEntity<Void> updateConsultantSkillLevel(
            @PathVariable Long consultantId,
            @PathVariable Long skillId,
            @PathVariable int newLevel) {
        consultantService.updateConsultantSkillLevel(consultantId, skillId, SkillLevel.values()[newLevel - 1]);
        return ResponseEntity.ok().build();
    }
}
