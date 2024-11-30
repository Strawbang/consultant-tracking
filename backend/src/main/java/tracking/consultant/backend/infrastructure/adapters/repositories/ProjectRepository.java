package tracking.consultant.backend.infrastructure.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracking.consultant.backend.infrastructure.entities.ProjectEntity;
import tracking.consultant.backend.infrastructure.entities.ConsultantEntity;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;
import tracking.consultant.backend.infrastructure.enums.ProjectStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @Query("SELECT p FROM ProjectEntity p JOIN p.requiredSkills s WHERE s.id = :skillId")
    List<ProjectEntity> findByRequiredSkillId(@Param("skillId") Long skillId);
    
    @Query("SELECT c FROM ProjectEntity p JOIN p.consultants c WHERE p.id = :projectId")
    Set<ConsultantEntity> findProjectConsultants(@Param("projectId") Long projectId);
    
    @Query("SELECT p FROM ProjectEntity p WHERE p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE")
    List<ProjectEntity> findActiveProjects();
    
    @Query("SELECT p FROM ProjectEntity p WHERE SIZE(p.consultants) < p.teamSize")
    List<ProjectEntity> findProjectsNeedingConsultants();
    
    List<ProjectEntity> findByConsultants_Id(Long consultantId);

    @Query("SELECT p FROM ProjectEntity p JOIN p.consultants c WHERE c = :consultant")
    List<ProjectEntity> findByConsultant(@Param("consultant") ConsultantEntity consultant);

    @Query("SELECT p FROM ProjectEntity p WHERE p.status = :status")
    List<ProjectEntity> findByStatus(@Param("status") ProjectStatus status);

    @Query("SELECT p FROM ProjectEntity p JOIN p.requiredSkills s WHERE s = :skill")
    List<ProjectEntity> findByRequiredSkill(@Param("skill") SkillEntity skill);

    @Query("SELECT p FROM ProjectEntity p WHERE p.startDate >= :startDate AND p.endDate <= :endDate")
    List<ProjectEntity> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM ProjectEntity p JOIN p.requiredSkills s WHERE p.id = :projectId")
    Set<SkillEntity> findProjectRequiredSkills(@Param("projectId") Long projectId);
}
