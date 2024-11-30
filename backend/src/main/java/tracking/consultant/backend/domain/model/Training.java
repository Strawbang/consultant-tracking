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
public class Training {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private TrainingStatus status;
    private String provider;
    private String certification;
    private int maxParticipants;
    private Set<Consultant> participants = new HashSet<>();
    private Set<Consultant> completedParticipants = new HashSet<>();
    private Set<Skill> skillsTaught = new HashSet<>();

    public boolean isCompleted() {
        return status == TrainingStatus.COMPLETED;
    }

    public boolean isInProgress() {
        return status == TrainingStatus.IN_PROGRESS;
    }

    public boolean isUpcoming() {
        return status == TrainingStatus.PLANNED && 
               startDate != null && 
               startDate.isAfter(LocalDate.now());
    }

    public int getDurationInDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    public Set<Consultant> getParticipants() {
        return participants;
    }

    public Set<Consultant> getCompletedParticipants() {
        return completedParticipants;
    }

    public Set<Skill> getSkillsTaught() {
        return skillsTaught;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void addParticipant(Consultant consultant) {
        participants.add(consultant);
    }

    public void removeParticipant(Consultant consultant) {
        participants.remove(consultant);
        completedParticipants.remove(consultant);
    }

    public void markParticipantAsCompleted(Consultant consultant) {
        if (participants.contains(consultant)) {
            completedParticipants.add(consultant);
        }
    }

    public void addSkill(Skill skill, int minimumLevel) {
        // Add the skill to skillsTaught
        skillsTaught.add(skill);
        
        // Optionally, you might want to store the minimum level 
        // This would require adding a new data structure to track skill levels
        // For now, we'll just add the skill
    }

    public void addSkillTaught(Skill skill) {
        skillsTaught.add(skill);
    }

    public void removeSkillTaught(Skill skill) {
        skillsTaught.remove(skill);
    }
}
