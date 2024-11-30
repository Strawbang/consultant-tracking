package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
@Data
@EqualsAndHashCode(exclude = {"consultant", "requiredSkills"})
@ToString(exclude = {"consultant", "requiredSkills"})
public class MissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "consultant_id", nullable = false)
    private ConsultantEntity consultant;

    @Column(nullable = false)
    private String client;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status = MissionStatus.PLANNED;

    @ManyToMany
    @JoinTable(
        name = "mission_required_skills",
        joinColumns = @JoinColumn(name = "mission_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> requiredSkills = new HashSet<>();

    private String role;

    private String responsibilities;

    @Column(nullable = false)
    private double allocationPercentage = 100.0;

    private String deliverables;

    private String feedback;

    public enum MissionStatus {
        PLANNED,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }
}
