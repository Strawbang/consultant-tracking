package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@EqualsAndHashCode(exclude = {"consultants", "requiredSkills"})
@ToString(exclude = {"consultants", "requiredSkills"})
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private String client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.PLANNED;

    @OneToMany(mappedBy = "currentProject")
    private Set<ConsultantEntity> consultants = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "project_required_skills",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> requiredSkills = new HashSet<>();

    private Integer teamSize;

    @Column(nullable = false)
    private String location;

    private String technologies;

    private String methodology; // Agile, Waterfall, etc.

    public enum ProjectStatus {
        PLANNED,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }
}
