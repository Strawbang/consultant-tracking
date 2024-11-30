package tracking.consultant.backend.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tracking.consultant.backend.domain.model.Training;
import tracking.consultant.backend.domain.model.TrainingStatus;
import tracking.consultant.backend.infrastructure.mappers.TrainingMapper;
import tracking.consultant.backend.infrastructure.ports.TrainingRepositoryPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainingRepositoryAdapter implements TrainingRepositoryPort {
    private final TrainingRepository repository;
    private final TrainingMapper mapper;

    @Override
    public Training save(Training training) {
        return mapper.toModel(repository.save(mapper.toEntity(training)));
    }

    @Override
    public Optional<Training> findById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public List<Training> findAll() {
        return repository.findAll().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Training> findByConsultantId(Long consultantId) {
        return repository.findByParticipants_Id(consultantId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findBySkillId(Long skillId) {
        return repository.findBySkillsTaught_Id(skillId).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findByStatus(TrainingStatus status) {
        return repository.findByStatus(status.name()).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    public List<Training> findAvailableTrainings() {
        return repository.findAvailableTrainings().stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findByConsultantIdAndStatus(Long consultantId, TrainingStatus status) {
        return repository.findByParticipants_IdAndStatus(consultantId, status.name()).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findUpcomingByConsultantId(Long consultantId) {
        return repository.findByParticipants_IdAndStatus(consultantId, TrainingStatus.PLANNED.name()).stream()
            .map(mapper::toModel)
            .collect(Collectors.toList());
    }

    @Override
    public List<Training> findCompletedByConsultantId(Long consultantId) {
        return repository.findByParticipants_IdAndStatus(consultantId, TrainingStatus.COMPLETED.name()).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    public void delete(Training training) {
        repository.delete(mapper.toEntity(training));
    }

    @Override
    public List<Training> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateBetween(startDate, endDate).stream()
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public int countByConsultantId(Long consultantId) {
        return repository.findByParticipants_Id(consultantId).size();
    }

    @Override
    public List<Training> findUpcoming() {
        return repository.findByStatus(TrainingStatus.PLANNED.name()).stream()
                .filter(training -> training.getStartDate().isAfter(LocalDate.now()))
                .map(mapper::toModel)
                .collect(Collectors.toList());
    }
}
