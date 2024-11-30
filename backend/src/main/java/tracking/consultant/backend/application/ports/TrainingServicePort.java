package tracking.consultant.backend.application.ports;

import tracking.consultant.backend.domain.model.Training;
import tracking.consultant.backend.domain.model.TrainingStatus;
import tracking.consultant.backend.domain.model.Skill;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingServicePort {
    Training createTraining(Training training);
    Optional<Training> getTrainingById(Long id);
    List<Training> getAllTrainings();
    List<Training> getTrainingsByConsultant(Long consultantId);
    List<Training> getTrainingsByStatus(TrainingStatus status);
    List<Training> getTrainingsBySkill(Long skillId);
    List<Training> getTrainingsByDateRange(LocalDate startDate, LocalDate endDate);
    List<Training> getUpcomingTrainings();
    List<Training> getTrainingsByConsultantAndStatus(Long consultantId, TrainingStatus status);
    int countTrainingsByConsultant(Long consultantId);
    List<Training> getCompletedTrainingsByConsultant(Long consultantId);
    List<Training> getUpcomingTrainingsByConsultant(Long consultantId);
    Training updateTraining(Long id, Training training);
    void deleteTraining(Long id);
    void registerConsultantForTraining(Long trainingId, Long consultantId);
    void unregisterConsultantFromTraining(Long trainingId, Long consultantId);
    void completeTraining(Long trainingId, Long consultantId);
    void cancelTraining(Long trainingId);
    boolean isTrainingFull(Long trainingId);
    int getAvailableSpots(Long trainingId);
    List<Skill> getTrainingSkills(Long trainingId);
    void addSkillToTrainingWithMinimumLevel(Long trainingId, Long skillId, int level);
    void removeSkillFromTraining(Long trainingId, Long skillId);
    void updateTrainingStatus(Long id, TrainingStatus status);
}
