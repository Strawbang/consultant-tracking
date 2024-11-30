package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import tracking.consultant.backend.domain.model.SkillCategory;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;

    private Integer level;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConsultantSkillEntity> consultantSkills = new HashSet<>();

    @ManyToMany(mappedBy = "requiredSkills")
    private Set<MissionEntity> missions = new HashSet<>();

    @ManyToMany(mappedBy = "skillsTaught")
    private Set<TrainingEntity> trainings = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "skill_prerequisites",
        joinColumns = @JoinColumn(name = "skill_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<SkillEntity> prerequisites = new HashSet<>();
}
