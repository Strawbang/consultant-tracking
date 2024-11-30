package tracking.consultant.backend.infrastructure.ports;

import tracking.consultant.backend.domain.model.Training;
import tracking.consultant.backend.domain.model.TrainingStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepositoryPort {
    Training save(Training training);
    Optional<Training> findById(Long id);
    List<Training> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Training> findByConsultantId(Long consultantId);
    List<Training> findByStatus(TrainingStatus status);
    List<Training> findBySkillId(Long skillId);
    List<Training> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Training> findUpcoming();
    List<Training> findByConsultantIdAndStatus(Long consultantId, TrainingStatus status);
    int countByConsultantId(Long consultantId);
    List<Training> findCompletedByConsultantId(Long consultantId);
    List<Training> findUpcomingByConsultantId(Long consultantId);
}
