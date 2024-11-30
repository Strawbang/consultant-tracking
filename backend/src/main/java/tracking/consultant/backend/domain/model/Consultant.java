package tracking.consultant.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultant {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String title;
    private double activityRate;
    private String careerGoals;
    private int maxSimultaneousProjects = 1;
    
    private Set<Project> projects = new HashSet<>();
    private Map<Skill, SkillLevel> skills = new HashMap<>();
    private Set<Training> trainings = new HashSet<>();
    private Set<Evaluation> evaluations = new HashSet<>();
    private Set<Mission> missions = new HashSet<>();
    
    public void addSkill(Skill skill, SkillLevel level) {
        skills.put(skill, level);
    }
    
    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }
    
    public void updateSkillLevel(Skill skill, SkillLevel level) {
        if (skills.containsKey(skill)) {
            skills.put(skill, level);
        }
    }
    
    public void addTraining(Training training) {
        trainings.add(training);
    }
    
    public void removeTraining(Training training) {
        trainings.remove(training);
    }
    
    public void addEvaluation(Evaluation evaluation) {
        evaluations.add(evaluation);
    }
    
    public void removeEvaluation(Evaluation evaluation) {
        evaluations.remove(evaluation);
    }
    
    public void addMission(Mission mission) {
        missions.add(mission);
    }
    
    public void removeMission(Mission mission) {
        missions.remove(mission);
    }
    
    public void addProject(Project project) {
        projects.add(project);
    }
    
    public void removeProject(Project project) {
        projects.remove(project);
    }
    
    public Set<Project> getProjects() {
        return projects;
    }
    
    public int getMaxSimultaneousProjects() {
        return maxSimultaneousProjects;
    }
    
    public void setMaxSimultaneousProjects(int maxSimultaneousProjects) {
        this.maxSimultaneousProjects = maxSimultaneousProjects;
    }
    
    public boolean hasSkill(Skill skill) {
        return skills.containsKey(skill);
    }

    public SkillLevel getSkillLevel(Skill skill) {
        return skills.get(skill);
    }

    public Set<Skill> getSkills() {
        return skills.keySet();
    }

    public Map<Skill, SkillLevel> getSkillsWithLevels() {
        return skills;
    }

    public boolean hasSkillWithMinimumLevel(Skill skill, int minimumLevel) {
        return skills.entrySet().stream()
            .filter(s -> s.getKey().getId().equals(skill.getId()))
            .anyMatch(s -> s.getValue().getValue() >= minimumLevel);
    }
    
    public void completeTraining(Training training) {
        if (!trainings.contains(training)) {
            throw new IllegalStateException("Le consultant n'est pas inscrit à cette formation");
        }
        training.setStatus(TrainingStatus.COMPLETED);
    }
}
