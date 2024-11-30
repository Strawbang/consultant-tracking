package tracking.consultant.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mission {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Consultant consultant;
    private String client;
    private String location;
    private MissionStatus status;
    
    private Set<Skill> requiredSkills = new HashSet<>();
    
    private String role;
    private String responsibilities;
    private double allocationPercentage;
    private String deliverables;
    private String feedback;

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return status == MissionStatus.IN_PROGRESS &&
               startDate.isBefore(now) &&
               (endDate == null || endDate.isAfter(now));
    }

    public boolean isOverlapping(Mission other) {
        if (this.startDate == null || other.startDate == null) {
            return false;
        }

        LocalDate thisEnd = this.endDate != null ? this.endDate : LocalDate.MAX;
        LocalDate otherEnd = other.endDate != null ? other.endDate : LocalDate.MAX;

        return !this.startDate.isAfter(otherEnd) && !thisEnd.isBefore(other.startDate);
    }

    public double getDurationInMonths() {
        if (startDate == null) {
            return 0;
        }
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        long days = startDate.until(end).getDays();
        return days / 30.0;
    }

    public boolean isCompleted() {
        return status == MissionStatus.COMPLETED;
    }

    public boolean requiresSkill(Skill skill) {
        return requiredSkills.contains(skill);
    }
}
