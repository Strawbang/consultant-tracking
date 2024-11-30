package tracking.consultant.backend.infrastructure.ports;

import tracking.consultant.backend.domain.model.Evaluation;
import tracking.consultant.backend.domain.model.EvaluationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepositoryPort {
    Evaluation save(Evaluation evaluation);
    Optional<Evaluation> findById(Long id);
    List<Evaluation> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Evaluation> findByConsultantId(Long consultantId);
    List<Evaluation> findByType(EvaluationType type);
    List<Evaluation> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Evaluation> findRecentEvaluations(LocalDate since);
    Optional<Evaluation> findMostRecentByConsultantAndType(Long consultantId, EvaluationType type);
    List<Evaluation> findByConsultantIdAndType(Long consultantId, EvaluationType type);
}
