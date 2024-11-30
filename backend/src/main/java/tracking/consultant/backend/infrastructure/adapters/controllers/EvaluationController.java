package tracking.consultant.backend.infrastructure.adapters.controllers;

import tracking.consultant.backend.application.ports.EvaluationServicePort;
import tracking.consultant.backend.domain.model.Evaluation;
import tracking.consultant.backend.domain.model.EvaluationType;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Evaluation", description = "API de gestion des évaluations des consultants")
public class EvaluationController {

    private final EvaluationServicePort evaluationService;

    @Operation(summary = "Créer une nouvelle évaluation",
            description = "Crée une nouvelle évaluation pour un consultant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation créée avec succès",
                content = @Content(schema = @Schema(implementation = Evaluation.class))),
        @ApiResponse(responseCode = "400", description = "Données d'évaluation invalides")
    })
    @PostMapping
    public ResponseEntity<Evaluation> createEvaluation(
            @Parameter(description = "Données de l'évaluation à créer", required = true)
            @RequestBody Evaluation evaluation) {
        return ResponseEntity.ok(evaluationService.createEvaluation(evaluation));
    }

    @Operation(summary = "Récupérer une évaluation par son ID",
            description = "Retourne une évaluation spécifique basée sur son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation trouvée",
                content = @Content(schema = @Schema(implementation = Evaluation.class))),
        @ApiResponse(responseCode = "404", description = "Évaluation non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(
            @Parameter(description = "ID de l'évaluation", required = true)
            @PathVariable Long id) {
        return evaluationService.getEvaluationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Récupérer toutes les évaluations",
            description = "Retourne la liste de toutes les évaluations")
    @ApiResponse(responseCode = "200", description = "Liste des évaluations récupérée avec succès",
            content = @Content(schema = @Schema(implementation = Evaluation.class)))
    @GetMapping
    public ResponseEntity<List<Evaluation>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @Operation(summary = "Récupérer les évaluations d'un consultant",
            description = "Retourne toutes les évaluations pour un consultant spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des évaluations du consultant récupérée avec succès",
            content = @Content(schema = @Schema(implementation = Evaluation.class)))
    @GetMapping("/consultant/{consultantId}")
    public ResponseEntity<List<Evaluation>> getEvaluationsByConsultant(
            @Parameter(description = "ID du consultant", required = true)
            @PathVariable Long consultantId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByConsultant(consultantId));
    }

    @Operation(summary = "Récupérer les évaluations par type",
            description = "Retourne toutes les évaluations d'un type spécifique")
    @ApiResponse(responseCode = "200", description = "Liste des évaluations du type spécifié récupérée avec succès",
            content = @Content(schema = @Schema(implementation = Evaluation.class)))
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Evaluation>> getEvaluationsByType(
            @Parameter(description = "Type d'évaluation", required = true)
            @PathVariable EvaluationType type) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByType(type));
    }

    @Operation(summary = "Récupérer les évaluations par période",
            description = "Retourne toutes les évaluations comprises dans une période donnée")
    @ApiResponse(responseCode = "200", description = "Liste des évaluations pour la période spécifiée",
            content = @Content(schema = @Schema(implementation = Evaluation.class)))
    @GetMapping("/date-range")
    public ResponseEntity<List<Evaluation>> getEvaluationsByDateRange(
            @Parameter(description = "Date de début de la période", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin de la période", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByDateRange(startDate, endDate));
    }

    @Operation(summary = "Récupérer les évaluations récentes",
            description = "Retourne toutes les évaluations depuis une date donnée")
    @ApiResponse(responseCode = "200", description = "Liste des évaluations récentes",
            content = @Content(schema = @Schema(implementation = Evaluation.class)))
    @GetMapping("/recent")
    public ResponseEntity<List<Evaluation>> getRecentEvaluations(
            @Parameter(description = "Date depuis laquelle récupérer les évaluations", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return ResponseEntity.ok(evaluationService.getRecentEvaluations(since));
    }

    @Operation(summary = "Récupérer l'évaluation la plus récente",
            description = "Retourne l'évaluation la plus récente d'un type spécifique pour un consultant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation la plus récente trouvée",
                content = @Content(schema = @Schema(implementation = Evaluation.class))),
        @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée")
    })
    @GetMapping("/consultant/{consultantId}/type/{type}/recent")
    public ResponseEntity<Evaluation> getMostRecentEvaluation(
            @Parameter(description = "ID du consultant", required = true)
            @PathVariable Long consultantId,
            @Parameter(description = "Type d'évaluation", required = true)
            @PathVariable EvaluationType type) {
        return evaluationService.getMostRecentEvaluation(consultantId, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Planifier une évaluation",
            description = "Planifie une nouvelle évaluation pour un consultant")
    @ApiResponse(responseCode = "200", description = "Évaluation planifiée avec succès")
    @PostMapping("/schedule")
    public ResponseEntity<Void> scheduleEvaluation(
            @Parameter(description = "ID du consultant", required = true)
            @RequestParam Long consultantId,
            @Parameter(description = "Type d'évaluation", required = true)
            @RequestParam EvaluationType type,
            @Parameter(description = "Date planifiée de l'évaluation", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduledDate) {
        evaluationService.scheduleEvaluation(consultantId, type, scheduledDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Compléter une évaluation",
            description = "Marque une évaluation comme complétée avec un score et des commentaires")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation complétée avec succès"),
        @ApiResponse(responseCode = "404", description = "Évaluation non trouvée")
    })
    @PostMapping("/{evaluationId}/complete")
    public ResponseEntity<Void> completeEvaluation(
            @Parameter(description = "ID de l'évaluation", required = true)
            @PathVariable Long evaluationId,
            @Parameter(description = "Score de l'évaluation", required = true)
            @RequestParam int score,
            @Parameter(description = "Commentaires optionnels")
            @RequestParam(required = false) String comments) {
        evaluationService.completeEvaluation(evaluationId, score, comments);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Vérifier si une évaluation est due",
            description = "Vérifie si une évaluation d'un type spécifique est due pour un consultant")
    @ApiResponse(responseCode = "200", description = "Statut de l'évaluation récupéré avec succès")
    @GetMapping("/consultant/{consultantId}/type/{type}/due")
    public ResponseEntity<Boolean> isEvaluationDue(
            @Parameter(description = "ID du consultant", required = true)
            @PathVariable Long consultantId,
            @Parameter(description = "Type d'évaluation", required = true)
            @PathVariable EvaluationType type) {
        return ResponseEntity.ok(evaluationService.isEvaluationDue(consultantId, type));
    }

    @Operation(summary = "Mettre à jour une évaluation",
            description = "Met à jour une évaluation existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation mise à jour avec succès",
                content = @Content(schema = @Schema(implementation = Evaluation.class))),
        @ApiResponse(responseCode = "404", description = "Évaluation non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Evaluation> updateEvaluation(
            @Parameter(description = "ID de l'évaluation", required = true)
            @PathVariable Long id,
            @Parameter(description = "Données de l'évaluation à mettre à jour", required = true)
            @RequestBody Evaluation evaluation) {
        return ResponseEntity.ok(evaluationService.updateEvaluation(id, evaluation));
    }

    @Operation(summary = "Supprimer une évaluation",
            description = "Supprime une évaluation existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Évaluation supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Évaluation non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(
            @Parameter(description = "ID de l'évaluation", required = true)
            @PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.ok().build();
    }
}
