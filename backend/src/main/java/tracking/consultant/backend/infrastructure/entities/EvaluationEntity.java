package tracking.consultant.backend.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tracking.consultant.backend.domain.model.EvaluationStatus;
import tracking.consultant.backend.domain.model.EvaluationType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "evaluations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate evaluationDate;

    @Column(nullable = false)
    private String evaluator;

    private double score;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private ConsultantEntity consultant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationStatus status;

    private LocalDate completionDate;

    @ElementCollection
    @CollectionTable(
        name = "evaluation_skill_scores",
        joinColumns = @JoinColumn(name = "evaluation_id")
    )
    @MapKeyJoinColumn(name = "skill_id")
    @Column(name = "score")
    private Map<SkillEntity, Integer> skillScores = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Version
    private Long version;
}
