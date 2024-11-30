package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import tracking.consultant.backend.domain.model.TrainingStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainings")
@Data
@NoArgsConstructor
public class TrainingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingStatus status;

    private Integer maxParticipants;

    private String location;

    private String trainer;

    @ManyToMany
    @JoinTable(
        name = "training_participants",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "consultant_id")
    )
    private Set<ConsultantEntity> participants = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "training_completed_participants",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "consultant_id")
    )
    private Set<ConsultantEntity> completedParticipants = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "training_skills",
        joinColumns = @JoinColumn(name = "training_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> skillsTaught = new HashSet<>();
}
