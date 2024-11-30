package tracking.consultant.backend.application.ports;

import tracking.consultant.backend.domain.model.Evaluation;
import tracking.consultant.backend.domain.model.EvaluationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EvaluationServicePort {
    /**
     * Crée une nouvelle évaluation
     */
    Evaluation createEvaluation(Evaluation evaluation);

    /**
     * Récupère une évaluation par son identifiant
     */
    Optional<Evaluation> getEvaluationById(Long id);

    /**
     * Récupère toutes les évaluations
     */
    List<Evaluation> getAllEvaluations();

    /**
     * Récupère les évaluations d'un consultant
     */
    List<Evaluation> getEvaluationsByConsultant(Long consultantId);

    /**
     * Récupère les évaluations d'un type spécifique
     */
    List<Evaluation> getEvaluationsByType(EvaluationType type);

    /**
     * Récupère les évaluations dans une plage de dates
     */
    List<Evaluation> getEvaluationsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Récupère les évaluations récentes depuis une date donnée
     */
    List<Evaluation> getRecentEvaluations(LocalDate since);

    /**
     * Récupère l'évaluation la plus récente d'un consultant pour un type donné
     */
    Optional<Evaluation> getMostRecentEvaluation(Long consultantId, EvaluationType type);

    /**
     * Planifie une nouvelle évaluation
     */
    void scheduleEvaluation(Long consultantId, EvaluationType type, LocalDate scheduledDate);

    /**
     * Démarre une évaluation planifiée
     */
    void startEvaluation(Long evaluationId);

    /**
     * Complète une évaluation avec un score et des commentaires
     */
    void completeEvaluation(Long evaluationId, double score, String comments);

    /**
     * Rejette une évaluation avec un motif
     */
    void rejectEvaluation(Long evaluationId, String reason);

    /**
     * Annule une évaluation avec un motif
     */
    void cancelEvaluation(Long evaluationId, String reason);

    /**
     * Archive une évaluation
     */
    void archiveEvaluation(Long evaluationId);

    /**
     * Vérifie si une évaluation est due pour un consultant et un type donnés
     */
    boolean isEvaluationDue(Long consultantId, EvaluationType type);

    /**
     * Met à jour une évaluation existante
     */
    Evaluation updateEvaluation(Long id, Evaluation evaluation);

    /**
     * Supprime une évaluation
     */
    void deleteEvaluation(Long id);
}
