package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "consultants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"skills", "trainings", "evaluations", "missions", "currentProject"})
@ToString(exclude = {"skills", "trainings", "evaluations", "missions", "currentProject"})
public class ConsultantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDate hireDate;

    private String phoneNumber;

    private String title;

    @Column(nullable = false)
    private double activityRate = 1.0; // Taux d'activité (1.0 = 100%)

    private String careerGoals;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConsultantSkillEntity> skills = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "consultant_trainings",
        joinColumns = @JoinColumn(name = "consultant_id"),
        inverseJoinColumns = @JoinColumn(name = "training_id")
    )
    private Set<TrainingEntity> trainings = new HashSet<>();

    @OneToMany(mappedBy = "consultant")
    private Set<EvaluationEntity> evaluations = new HashSet<>();

    @OneToMany(mappedBy = "consultant")
    private Set<MissionEntity> missions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "current_project_id")
    private ProjectEntity currentProject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultantStatus status = ConsultantStatus.ACTIVE;

    private int maxSimultaneousProjects = 1;

    public enum ConsultantStatus {
        ACTIVE,
        INACTIVE,
        ON_LEAVE,
        TERMINATED
    }
}
