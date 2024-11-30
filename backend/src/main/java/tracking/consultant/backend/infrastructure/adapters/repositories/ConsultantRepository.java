package tracking.consultant.backend.infrastructure.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracking.consultant.backend.infrastructure.entities.ConsultantEntity;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;
import tracking.consultant.backend.domain.model.SkillLevel;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends JpaRepository<ConsultantEntity, Long> {
    List<ConsultantEntity> findBySkills_Skill_Id(Long skillId);
    
    @Query("SELECT c FROM ConsultantEntity c JOIN c.skills cs WHERE cs.skill = :skill AND cs.skillLevel >= :minimumLevel")
    List<ConsultantEntity> findBySkillAndMinimumLevel(@Param("skill") SkillEntity skill, @Param("minimumLevel") Integer minimumLevel);
    
    @Query("SELECT c FROM ConsultantEntity c JOIN c.skills cs WHERE cs.skill = :skill AND cs.skillLevel = :level")
    List<ConsultantEntity> findBySkillAndLevel(@Param("skill") SkillEntity skill, @Param("level") SkillLevel level);
    
    @Query("SELECT c FROM ConsultantEntity c WHERE c.currentProject.id = :projectId")
    List<ConsultantEntity> findByCurrentProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT c FROM ConsultantEntity c WHERE c.currentProject IS NULL")
    List<ConsultantEntity> findAvailableConsultants();
    
    Optional<ConsultantEntity> findByEmail(String email);
}
