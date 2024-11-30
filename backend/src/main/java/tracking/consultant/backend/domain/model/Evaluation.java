package tracking.consultant.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation {
    private Long id;
    private LocalDate evaluationDate;
    private String evaluator;
    private double score;
    private String comments;
    private Consultant consultant;
    private EvaluationStatus status = EvaluationStatus.DRAFT;
    private LocalDate completionDate;
    
    private Map<Skill, Integer> skillScores = new HashMap<>();
    
    private EvaluationType type;
    private Project project;

    public void addSkillScore(Skill skill, int score) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }
        skillScores.put(skill, score);
    }

    public double getAverageSkillScore() {
        if (skillScores.isEmpty()) {
            return 0.0;
        }
        return skillScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    public boolean isRecent() {
        return evaluationDate != null &&
               evaluationDate.isAfter(LocalDate.now().minusMonths(6));
    }

    public boolean isProjectEvaluation() {
        return type == EvaluationType.PROJECT && project != null;
    }

    public boolean isMonthly() {
        return type == EvaluationType.MONTHLY;
    }

    public boolean isQuarterly() {
        return type == EvaluationType.QUARTERLY;
    }

    public void complete() {
        this.status = EvaluationStatus.COMPLETED;
        this.completionDate = LocalDate.now();
    }

    public void reject() {
        this.status = EvaluationStatus.REJECTED;
    }

    public boolean isDraft() {
        return status == EvaluationStatus.DRAFT;
    }

    public boolean isCompleted() {
        return status == EvaluationStatus.COMPLETED;
    }

    public boolean isRejected() {
        return status == EvaluationStatus.REJECTED;
    }
}
