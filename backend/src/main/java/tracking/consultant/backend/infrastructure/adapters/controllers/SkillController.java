package tracking.consultant.backend.infrastructure.adapters.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracking.consultant.backend.application.ports.SkillServicePort;
import tracking.consultant.backend.domain.model.Skill;
import tracking.consultant.backend.domain.model.SkillCategory;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Tag(name = "Skill", description = "API pour la gestion des compétences")
public class SkillController {

    private final SkillServicePort skillService;

    @PostMapping
    @Operation(summary = "Créer une compétence")
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.createSkill(skill));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une compétence par son ID")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        return skillService.getSkillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les compétences")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Obtenir les compétences par catégorie")
    public ResponseEntity<List<Skill>> getSkillsByCategory(@PathVariable SkillCategory category) {
        return ResponseEntity.ok(skillService.getSkillsByCategory(category));
    }

    @GetMapping("/consultant/{consultantId}")
    @Operation(summary = "Obtenir les compétences d'un consultant")
    public ResponseEntity<List<Skill>> getSkillsByConsultant(@PathVariable Long consultantId) {
        return ResponseEntity.ok(skillService.getSkillsByConsultant(consultantId));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Obtenir les compétences requises pour un projet")
    public ResponseEntity<List<Skill>> getSkillsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(skillService.getSkillsByProject(projectId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une compétence")
    public ResponseEntity<Skill> updateSkill(
            @PathVariable Long id,
            @RequestBody Skill skill) {
        return ResponseEntity.ok(skillService.updateSkill(id, skill));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une compétence")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/category")
    @Operation(summary = "Assigner une compétence à une catégorie")
    public ResponseEntity<Void> assignSkillToCategory(
            @PathVariable Long skillId,
            @RequestBody SkillCategory category) {
        skillService.assignSkillToCategory(skillId, category);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{skillId}/category/{newCategory}")
    @Operation(summary = "Mettre à jour la catégorie d'une compétence")
    public ResponseEntity<Void> updateSkillCategory(
            @PathVariable Long skillId,
            @PathVariable SkillCategory newCategory) {
        skillService.updateSkillCategory(skillId, newCategory);
        return ResponseEntity.ok().build();
    }
}
