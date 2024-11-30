package tracking.consultant.backend.infrastructure.adapters.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tracking.consultant.backend.application.ports.TrainingServicePort;
import tracking.consultant.backend.domain.model.Training;
import tracking.consultant.backend.domain.model.TrainingStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Training", description = "API pour la gestion des formations")
public class TrainingController {

    private final TrainingServicePort trainingService;

    @PostMapping
    @Operation(summary = "Créer une formation")
    public ResponseEntity<Training> createTraining(@RequestBody Training training) {
        return ResponseEntity.ok(trainingService.createTraining(training));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une formation par son ID")
    public ResponseEntity<Training> getTrainingById(@PathVariable Long id) {
        return trainingService.getTrainingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Obtenir toutes les formations")
    public ResponseEntity<List<Training>> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }

    @GetMapping("/consultant/{consultantId}")
    @Operation(summary = "Obtenir les formations d'un consultant")
    public ResponseEntity<List<Training>> getTrainingsByConsultant(@PathVariable Long consultantId) {
        return ResponseEntity.ok(trainingService.getTrainingsByConsultant(consultantId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtenir les formations par statut")
    public ResponseEntity<List<Training>> getTrainingsByStatus(@PathVariable TrainingStatus status) {
        return ResponseEntity.ok(trainingService.getTrainingsByStatus(status));
    }

    @GetMapping("/skill/{skillId}")
    @Operation(summary = "Obtenir les formations par compétence")
    public ResponseEntity<List<Training>> getTrainingsBySkill(@PathVariable Long skillId) {
        return ResponseEntity.ok(trainingService.getTrainingsBySkill(skillId));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtenir les formations dans une plage de dates")
    public ResponseEntity<List<Training>> getTrainingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(trainingService.getTrainingsByDateRange(startDate, endDate));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Obtenir les formations à venir")
    public ResponseEntity<List<Training>> getUpcomingTrainings() {
        return ResponseEntity.ok(trainingService.getUpcomingTrainings());
    }

    @GetMapping("/completed/{consultantId}")
    @Operation(summary = "Obtenir les formations terminées d'un consultant")
    public ResponseEntity<List<Training>> getCompletedTrainingsByConsultant(@PathVariable Long consultantId) {
        return ResponseEntity.ok(trainingService.getCompletedTrainingsByConsultant(consultantId));
    }

    @GetMapping("/upcoming/{consultantId}")
    @Operation(summary = "Obtenir les formations à venir d'un consultant")
    public ResponseEntity<List<Training>> getUpcomingTrainingsByConsultant(@PathVariable Long consultantId) {
        return ResponseEntity.ok(trainingService.getUpcomingTrainingsByConsultant(consultantId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une formation")
    public ResponseEntity<Training> updateTraining(
            @PathVariable Long id,
            @RequestBody Training training) {
        return ResponseEntity.ok(trainingService.updateTraining(id, training));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une formation")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{trainingId}/participants/{consultantId}")
    @Operation(summary = "Ajouter un participant à une formation")
    public ResponseEntity<Void> registerConsultantForTraining(
            @PathVariable Long trainingId,
            @PathVariable Long consultantId) {
        trainingService.registerConsultantForTraining(trainingId, consultantId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{trainingId}/participants/{consultantId}")
    @Operation(summary = "Retirer un participant d'une formation")
    public ResponseEntity<Void> unregisterConsultantFromTraining(
            @PathVariable Long trainingId,
            @PathVariable Long consultantId) {
        trainingService.unregisterConsultantFromTraining(trainingId, consultantId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status/{status}")
    @Operation(summary = "Mettre à jour le statut d'une formation")
    public ResponseEntity<Void> updateTrainingStatus(
            @PathVariable Long id,
            @PathVariable TrainingStatus status) {
        trainingService.updateTrainingStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
