package tracking.consultant.backend.infrastructure.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracking.consultant.backend.infrastructure.entities.EvaluationEntity;
import tracking.consultant.backend.domain.model.EvaluationType;
import java.time.LocalDate;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Long> {
    List<EvaluationEntity> findByConsultantId(Long consultantId);
    
    @Query("SELECT e FROM EvaluationEntity e WHERE e.consultant.id = :consultantId AND e.type = :type")
    List<EvaluationEntity> findByConsultantIdAndType(
        @Param("consultantId") Long consultantId, 
        @Param("type") EvaluationType type
    );
    
    @Query("SELECT e FROM EvaluationEntity e JOIN e.skillScores ss WHERE KEY(ss).id = :skillId")
    List<EvaluationEntity> findBySkillId(@Param("skillId") Long skillId);

    @Query("SELECT e FROM EvaluationEntity e WHERE e.evaluationDate BETWEEN :startDate AND :endDate")
    List<EvaluationEntity> findByDateRange(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
}
