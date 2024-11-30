package tracking.consultant.backend.infrastructure.adapters.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tracking.consultant.backend.domain.model.SkillCategory;
import tracking.consultant.backend.infrastructure.entities.SkillEntity;

import java.util.List;

public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    List<SkillEntity> findByCategory(SkillCategory category);
    
    @Query("SELECT DISTINCT s FROM ConsultantEntity c JOIN c.skills cs JOIN cs.skill s WHERE c.id = :consultantId")
    List<SkillEntity> findByConsultantId(@Param("consultantId") Long consultantId);
    
    @Query("SELECT DISTINCT s FROM ProjectEntity p JOIN p.requiredSkills s WHERE p.id = :projectId")
    List<SkillEntity> findByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT s FROM SkillEntity s WHERE s.prerequisites IS EMPTY")
    List<SkillEntity> findBaseSkills();
    
    @Query("SELECT s FROM SkillEntity s WHERE :prerequisite MEMBER OF s.prerequisites")
    List<SkillEntity> findByPrerequisite(@Param("prerequisite") SkillEntity prerequisite);
}
