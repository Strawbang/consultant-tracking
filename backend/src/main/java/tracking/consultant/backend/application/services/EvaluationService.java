package tracking.consultant.backend.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracking.consultant.backend.application.ports.EvaluationServicePort;
import tracking.consultant.backend.domain.model.*;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.EvaluationRepositoryPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationService implements EvaluationServicePort {

    private final EvaluationRepositoryPort evaluationRepository;
    private final ConsultantRepositoryPort consultantRepository;

    private void validateEvaluation(Evaluation evaluation) {
        if (evaluation.getScore() < 0 || evaluation.getScore() > 5) {
            throw new IllegalStateException("Le score doit être compris entre 0 et 5");
        }

        if (evaluation.getEvaluationDate() == null) {
            throw new IllegalStateException("La date d'évaluation est obligatoire");
        }

        if (evaluation.getEvaluationDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("La date d'évaluation ne peut pas être dans le futur");
        }

        if (evaluation.getEvaluator() == null || evaluation.getEvaluator().trim().isEmpty()) {
            throw new IllegalStateException("L'évaluateur est obligatoire");
        }

        if (evaluation.getConsultant() == null) {
            throw new IllegalStateException("Le consultant évalué est obligatoire");
        }

        if (evaluation.getType() == null) {
            throw new IllegalStateException("Le type d'évaluation est obligatoire");
        }
    }

    private void checkEvaluationFrequency(Consultant consultant, EvaluationType type, LocalDate evaluationDate) {
        Optional<Evaluation> recentEvaluation = evaluationRepository.findMostRecentByConsultantAndType(
            consultant.getId(), type);

        if (recentEvaluation.isPresent()) {
            LocalDate lastEvaluationDate = recentEvaluation.get().getEvaluationDate();
            
            switch (type) {
                case MONTHLY:
                    if (lastEvaluationDate.plusMonths(1).isAfter(evaluationDate)) {
                        throw new IllegalStateException("Une évaluation mensuelle existe déjà pour ce mois");
                    }
                    break;
                case QUARTERLY:
                    if (lastEvaluationDate.plusMonths(3).isAfter(evaluationDate)) {
                        throw new IllegalStateException("Une évaluation trimestrielle existe déjà pour ce trimestre");
                    }
                    break;
                case ANNUAL:
                case PROJECT:
                case PERFORMANCE:
                case SKILLS:
                    // For these types, we'll allow multiple evaluations without time restrictions
                    break;
                default:
                    throw new IllegalStateException("Type d'évaluation non pris en charge: " + type);
            }
        }
    }

    @Override
    public Evaluation createEvaluation(Evaluation evaluation) {
        validateEvaluation(evaluation);
        checkEvaluationFrequency(evaluation.getConsultant(), evaluation.getType(), evaluation.getEvaluationDate());
        evaluation.setStatus(EvaluationStatus.DRAFT);
        return evaluationRepository.save(evaluation);
    }

    @Override
    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Override
    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public List<Evaluation> getEvaluationsByConsultant(Long consultantId) {
        return evaluationRepository.findByConsultantId(consultantId);
    }

    @Override
    public List<Evaluation> getEvaluationsByType(EvaluationType type) {
        return evaluationRepository.findByType(type);
    }

    @Override
    public List<Evaluation> getEvaluationsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début doit être antérieure à la date de fin");
        }
        return evaluationRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Evaluation> getRecentEvaluations(LocalDate since) {
        return evaluationRepository.findRecentEvaluations(since);
    }

    @Override
    public Optional<Evaluation> getMostRecentEvaluation(Long consultantId, EvaluationType type) {
        return evaluationRepository.findMostRecentByConsultantAndType(consultantId, type);
    }

    @Override
    @Transactional
    public void scheduleEvaluation(Long consultantId, EvaluationType type, LocalDate scheduledDate) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));

        if (scheduledDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date planifiée ne peut pas être dans le passé");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setConsultant(consultant);
        evaluation.setType(type);
        evaluation.setEvaluationDate(scheduledDate);
        evaluation.setStatus(EvaluationStatus.SCHEDULED);

        evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void startEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + evaluationId));

        if (!evaluation.getStatus().canTransitionTo(EvaluationStatus.IN_PROGRESS)) {
            throw new IllegalStateException("L'évaluation ne peut pas être démarrée depuis son état actuel: " + evaluation.getStatus());
        }

        evaluation.setStatus(EvaluationStatus.IN_PROGRESS);
        evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void completeEvaluation(Long evaluationId, double score, String comments) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + evaluationId));

        if (!evaluation.getStatus().canTransitionTo(EvaluationStatus.COMPLETED)) {
            throw new IllegalStateException("L'évaluation ne peut pas être terminée depuis son état actuel: " + evaluation.getStatus());
        }

        if (score < 0 || score > 5) {
            throw new IllegalArgumentException("Le score doit être compris entre 0 et 5");
        }

        evaluation.setScore(score);
        evaluation.setComments(comments);
        evaluation.setStatus(EvaluationStatus.COMPLETED);
        evaluation.setCompletionDate(LocalDate.now());

        evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void rejectEvaluation(Long evaluationId, String reason) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + evaluationId));

        if (!evaluation.getStatus().canTransitionTo(EvaluationStatus.REJECTED)) {
            throw new IllegalStateException("L'évaluation ne peut pas être rejetée depuis son état actuel: " + evaluation.getStatus());
        }

        evaluation.setStatus(EvaluationStatus.REJECTED);
        evaluation.setComments(evaluation.getComments() + "\nMotif du rejet: " + reason);

        evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void cancelEvaluation(Long evaluationId, String reason) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + evaluationId));

        if (!evaluation.getStatus().canTransitionTo(EvaluationStatus.CANCELLED)) {
            throw new IllegalStateException("L'évaluation ne peut pas être annulée depuis son état actuel: " + evaluation.getStatus());
        }

        evaluation.setStatus(EvaluationStatus.CANCELLED);
        evaluation.setComments(evaluation.getComments() + "\nMotif d'annulation: " + reason);

        evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void archiveEvaluation(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + evaluationId));

        if (!evaluation.getStatus().canTransitionTo(EvaluationStatus.ARCHIVED)) {
            throw new IllegalStateException("L'évaluation ne peut pas être archivée depuis son état actuel: " + evaluation.getStatus());
        }

        evaluation.setStatus(EvaluationStatus.ARCHIVED);
        evaluationRepository.save(evaluation);
    }

    @Override
    public boolean isEvaluationDue(Long consultantId, EvaluationType type) {
        Optional<Evaluation> lastEvaluation = evaluationRepository.findMostRecentByConsultantAndType(consultantId, type);

        if (lastEvaluation.isEmpty()) {
            return true;
        }

        LocalDate lastEvaluationDate = lastEvaluation.get().getEvaluationDate();
        LocalDate now = LocalDate.now();

        switch (type) {
            case MONTHLY:
                return lastEvaluationDate.plusMonths(1).isBefore(now);
            case QUARTERLY:
                return lastEvaluationDate.plusMonths(3).isBefore(now);
            case ANNUAL:
                return lastEvaluationDate.plusYears(1).isBefore(now);
            default:
                return false;
        }
    }

    @Override
    @Transactional
    public Evaluation updateEvaluation(Long id, Evaluation evaluation) {
        Evaluation existingEvaluation = evaluationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + id));
        
        if (!existingEvaluation.getStatus().isEditable()) {
            throw new IllegalStateException("L'évaluation ne peut pas être modifiée dans son état actuel: " + existingEvaluation.getStatus());
        }

        validateEvaluation(evaluation);
        evaluation.setId(id);
        evaluation.setStatus(existingEvaluation.getStatus());
        return evaluationRepository.save(evaluation);
    }

    @Override
    @Transactional
    public void deleteEvaluation(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Évaluation non trouvée avec l'id: " + id));

        if (!evaluation.getStatus().isEditable()) {
            throw new IllegalStateException("L'évaluation ne peut pas être supprimée dans son état actuel: " + evaluation.getStatus());
        }

        evaluationRepository.deleteById(id);
    }
}
