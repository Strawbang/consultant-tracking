package tracking.consultant.backend.infrastructure.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracking.consultant.backend.infrastructure.entities.TrainingEntity;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository extends JpaRepository<TrainingEntity, Long> {
    List<TrainingEntity> findByParticipants_Id(Long consultantId);
    
    List<TrainingEntity> findBySkillsTaught_Id(Long skillId);
    
    @Query("SELECT t FROM TrainingEntity t WHERE t.status = :status")
    List<TrainingEntity> findByStatus(@Param("status") String status);
    
    @Query("SELECT t FROM TrainingEntity t WHERE SIZE(t.participants) < t.maxParticipants")
    List<TrainingEntity> findAvailableTrainings();
    
    @Query("SELECT t FROM TrainingEntity t JOIN t.participants c WHERE c.id = :consultantId AND t.status = :status")
    List<TrainingEntity> findByParticipants_IdAndStatus(@Param("consultantId") Long consultantId, @Param("status") String status);
    
    @Query("SELECT t FROM TrainingEntity t WHERE t.startDate >= :startDate AND t.endDate <= :endDate")
    List<TrainingEntity> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
