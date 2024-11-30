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
public class Project {
    private Long id;
    private String name;
    private String description;
    private String client;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    
    private Set<RequiredSkill> requiredSkills = new HashSet<>();
    private Set<Consultant> consultants = new HashSet<>();

    public void validateDates() {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin du projet sont obligatoires");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début du projet ne peut pas être postérieure à la date de fin");
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de début du projet ne peut pas être dans le passé");
        }
    }

    public boolean isActive() {
        return status == ProjectStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETED;
    }

    public void addConsultant(Consultant consultant) {
        if (consultants == null) {
            consultants = new HashSet<>();
        }
        if (!consultants.add(consultant)) {
            throw new IllegalArgumentException("Le consultant est déjà assigné à ce projet");
        }
        consultant.addProject(this);
    }

    public void removeConsultant(Consultant consultant) {
        if (consultants == null || !consultants.remove(consultant)) {
            throw new IllegalArgumentException("Le consultant n'est pas assigné à ce projet");
        }
        consultant.removeProject(this);
    }

    public void addRequiredSkill(Skill skill, int minimumLevel) {
        if (minimumLevel < 1 || minimumLevel > 5) {
            throw new IllegalArgumentException("Le niveau minimum doit être compris entre 1 et 5");
        }
        
        if (hasRequiredSkill(skill)) {
            throw new IllegalArgumentException("Cette compétence est déjà requise pour ce projet");
        }
        
        requiredSkills.add(new RequiredSkill(skill, minimumLevel));
    }

    public void removeRequiredSkill(Skill skill) {
        if (!hasRequiredSkill(skill)) {
            throw new IllegalArgumentException("Cette compétence n'est pas requise pour ce projet");
        }
        requiredSkills.removeIf(rs -> rs.getId().equals(skill.getId()));
    }

    public boolean hasRequiredSkill(Skill skill) {
        return requiredSkills.stream().anyMatch(rs -> rs.getId().equals(skill.getId()));
    }

    public RequiredSkill getRequiredSkill(Skill skill) {
        return requiredSkills.stream()
            .filter(rs -> rs.getId().equals(skill.getId()))
            .findFirst()
            .orElse(null);
    }

    public boolean isConsultantQualified(Consultant consultant) {
        return requiredSkills.stream().allMatch(required -> 
            consultant.hasSkillWithMinimumLevel(required, required.getMinimumLevel())
        );
    }

    public double calculateProgress() {
        switch (status) {
            case NOT_STARTED:
                return 0.0;
            case IN_PROGRESS:
                long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now());
                return Math.min(0.9, Math.max(0.1, (double) daysElapsed / totalDays));
            case COMPLETED:
                return 1.0;
            case CANCELLED:
                return 0.0;
            default:
                return 0.0;
        }
    }

    public Set<Consultant> getConsultants() {
        return consultants;
    }

    public Set<RequiredSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void updateStatus(ProjectStatus newStatus) {
        this.status = newStatus;
    }

    public boolean hasConsultant(Consultant consultant) {
        return consultants.contains(consultant);
    }

    public int getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }
}
