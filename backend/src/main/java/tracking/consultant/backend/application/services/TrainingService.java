package tracking.consultant.backend.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracking.consultant.backend.application.ports.TrainingServicePort;
import tracking.consultant.backend.domain.model.*;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.TrainingRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingService implements TrainingServicePort {

    private final TrainingRepositoryPort trainingRepository;
    private final ConsultantRepositoryPort consultantRepository;
    private final SkillRepositoryPort skillRepository;

    private void validateTraining(Training training) {
        if (training.getName() == null || training.getName().trim().isEmpty()) {
            throw new IllegalStateException("Le nom de la formation est obligatoire");
        }

        if (training.getStartDate() == null) {
            throw new IllegalStateException("La date de début est obligatoire");
        }

        if (training.getEndDate() == null) {
            throw new IllegalStateException("La date de fin est obligatoire");
        }

        if (training.getStartDate().isAfter(training.getEndDate())) {
            throw new IllegalStateException("La date de début ne peut pas être après la date de fin");
        }

        if (training.getMaxParticipants() > 0) {
            if (training.getParticipants().size() > training.getMaxParticipants()) {
                throw new IllegalStateException("Le nombre maximum de participants est dépassé");
            }
        }
    }

    private boolean isConsultantAvailableForTraining(Consultant consultant, Training training) {
        // Vérifier les chevauchements avec d'autres formations
        boolean hasTrainingConflict = consultant.getTrainings().stream()
            .anyMatch(existingTraining -> 
                !existingTraining.getId().equals(training.getId()) &&
                existingTraining.getStartDate().isBefore(training.getEndDate()) &&
                existingTraining.getEndDate().isAfter(training.getStartDate())
            );

        // Vérifier les chevauchements avec des projets
        boolean hasProjectConflict = consultant.getProjects().stream()
            .anyMatch(project -> 
                project.getStartDate().isBefore(training.getEndDate()) &&
                project.getEndDate().isAfter(training.getStartDate())
            );

        return !hasTrainingConflict && !hasProjectConflict;
    }

    @Override
    public Training createTraining(Training training) {
        validateTraining(training);
        training.setStatus(TrainingStatus.PLANNED);
        return trainingRepository.save(training);
    }

    @Override
    public Optional<Training> getTrainingById(Long id) {
        return trainingRepository.findById(id);
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> getTrainingsByConsultant(Long consultantId) {
        return trainingRepository.findByConsultantId(consultantId);
    }

    @Override
    public List<Training> getTrainingsByStatus(TrainingStatus status) {
        return trainingRepository.findByStatus(status);
    }

    @Override
    public List<Training> getTrainingsBySkill(Long skillId) {
        skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));
        return trainingRepository.findBySkillId(skillId);
    }

    @Override
    public List<Training> getTrainingsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
        return trainingRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Training> getUpcomingTrainings() {
        return trainingRepository.findUpcoming();
    }

    @Override
    public List<Training> getTrainingsByConsultantAndStatus(Long consultantId, TrainingStatus status) {
        return trainingRepository.findByConsultantIdAndStatus(consultantId, status);
    }

    @Override
    public Training updateTraining(Long id, Training training) {
        Training existingTraining = trainingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + id));
        
        if (!existingTraining.getStatus().isEditable()) {
            throw new IllegalStateException("La formation ne peut pas être modifiée dans son état actuel: " + existingTraining.getStatus());
        }

        validateTraining(training);
        training.setId(id);
        training.setStatus(existingTraining.getStatus());
        return trainingRepository.save(training);
    }

    @Override
    public void deleteTraining(Long id) {
        Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + id));

        if (!training.getStatus().isEditable()) {
            throw new IllegalStateException("La formation ne peut pas être supprimée dans son état actuel: " + training.getStatus());
        }

        trainingRepository.deleteById(id);
    }

    @Override
    public void registerConsultantForTraining(Long trainingId, Long consultantId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));

        if (!training.getStatus().allowsRegistration()) {
            throw new IllegalStateException("Les inscriptions ne sont pas ouvertes pour cette formation");
        }

        if (!isConsultantAvailableForTraining(consultant, training)) {
            throw new IllegalStateException("Le consultant n'est pas disponible pour cette formation");
        }

        if (training.getMaxParticipants() > 0 && training.getParticipants().size() >= training.getMaxParticipants()) {
            throw new IllegalStateException("La formation a atteint sa capacité maximale");
        }

        training.addParticipant(consultant);
        trainingRepository.save(training);
        consultantRepository.save(consultant);
    }

    @Override
    public void unregisterConsultantFromTraining(Long trainingId, Long consultantId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));

        if (!training.getStatus().allowsUnregistration()) {
            throw new IllegalStateException("Le désistement n'est pas possible dans l'état actuel de la formation");
        }

        training.removeParticipant(consultant);
        trainingRepository.save(training);
        consultantRepository.save(consultant);
    }

    @Override
    public void updateTrainingStatus(Long trainingId, TrainingStatus newStatus) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        if (!training.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException("La formation ne peut pas passer de l'état " + 
                training.getStatus() + " à l'état " + newStatus);
        }

        training.setStatus(newStatus);
        
        if (newStatus == TrainingStatus.COMPLETED) {
            training.getParticipants().forEach(consultant -> {
                training.getSkillsTaught().forEach(skill -> {
                    if (!consultant.hasSkill(skill)) {
                        consultant.addSkill(skill, SkillLevel.BEGINNER);
                    } else {
                        SkillLevel currentLevel = consultant.getSkillLevel(skill);
                        if (currentLevel.ordinal() < SkillLevel.EXPERT.ordinal()) {
                            consultant.updateSkillLevel(skill, SkillLevel.values()[currentLevel.ordinal() + 1]);
                        }
                    }
                });
                consultantRepository.save(consultant);
            });
        }
        
        trainingRepository.save(training);
    }

    public void addSkillToTraining(Long trainingId, Long skillId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));

        if (!training.getStatus().isEditable()) {
            throw new IllegalStateException("Les compétences ne peuvent pas être modifiées dans l'état actuel de la formation");
        }

        training.addSkillTaught(skill);
        trainingRepository.save(training);
    }

    @Override
    public void removeSkillFromTraining(Long trainingId, Long skillId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));

        if (!training.getStatus().isEditable()) {
            throw new IllegalStateException("Les compétences ne peuvent pas être modifiées dans l'état actuel de la formation");
        }

        training.removeSkillTaught(skill);
        trainingRepository.save(training);
    }

    public List<Skill> getTrainingSkills(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        return new ArrayList<>(training.getSkillsTaught());
    }

    @Override
    public boolean isTrainingFull(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        return training.getMaxParticipants() > 0 && training.getParticipants().size() >= training.getMaxParticipants();
    }

    @Override
    public void addSkillToTrainingWithMinimumLevel(Long trainingId, Long skillId, int minimumLevel) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id: " + skillId));

        if (!training.getStatus().isEditable()) {
            throw new IllegalStateException("La formation ne peut pas être modifiée dans son état actuel");
        }

        training.addSkill(skill, minimumLevel);
        trainingRepository.save(training);
    }

    @Override
    public int getAvailableSpots(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        if (training.getMaxParticipants() <= 0) {
            return -1;
        }
        return training.getMaxParticipants() - training.getParticipants().size();
    }

    @Override
    public List<Training> getUpcomingTrainingsByConsultant(Long consultantId) {
        consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        return trainingRepository.findUpcomingByConsultantId(consultantId);
    }

    @Override
    public void cancelTraining(Long trainingId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        if (!training.getStatus().canTransitionTo(TrainingStatus.CANCELLED)) {
            throw new IllegalStateException("La formation ne peut pas être annulée dans son état actuel");
        }

        training.setStatus(TrainingStatus.CANCELLED);
        trainingRepository.save(training);
    }

    @Override
    public int countTrainingsByConsultant(Long consultantId) {
        return (int) trainingRepository.countByConsultantId(consultantId);
    }

    @Override
    public void completeTraining(Long trainingId, Long consultantId) {
        Training training = trainingRepository.findById(trainingId)
            .orElseThrow(() -> new IllegalArgumentException("Formation non trouvée avec l'id: " + trainingId));
        
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));

        if (!training.getParticipants().contains(consultant)) {
            throw new IllegalStateException("Le consultant n'est pas inscrit à cette formation");
        }

        if (training.getStatus() != TrainingStatus.IN_PROGRESS) {
            throw new IllegalStateException("La formation n'est pas en cours");
        }

        consultant.completeTraining(training);
        consultantRepository.save(consultant);
    }

    @Override
    public List<Training> getCompletedTrainingsByConsultant(Long consultantId) {
        consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id: " + consultantId));
        return trainingRepository.findCompletedByConsultantId(consultantId);
    }
}
