package tracking.consultant.backend.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tracking.consultant.backend.domain.model.Evaluation;
import tracking.consultant.backend.domain.model.EvaluationType;
import tracking.consultant.backend.infrastructure.entities.EvaluationEntity;
import tracking.consultant.backend.infrastructure.mappers.EvaluationMapper;
import tracking.consultant.backend.infrastructure.ports.EvaluationRepositoryPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvaluationRepositoryAdapter implements EvaluationRepositoryPort {
    private final EvaluationRepository repository;
    private final EvaluationMapper mapper;

    @Override
    public Evaluation save(Evaluation evaluation) {
        return mapper.toDomain(repository.save(mapper.toEntity(evaluation)));
    }

    @Override
    public Optional<Evaluation> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Evaluation> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Evaluation> findByConsultantId(Long consultantId) {
        return repository.findByConsultantId(consultantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Evaluation> findByConsultantIdAndType(Long consultantId, EvaluationType type) {
        return repository.findByConsultantIdAndType(consultantId, type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public List<Evaluation> findBySkillId(Long skillId) {
        return repository.findBySkillId(skillId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    public void delete(Evaluation evaluation) {
        repository.delete(mapper.toEntity(evaluation));
    }

    public List<Evaluation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<Evaluation> findRecentEvaluations(LocalDate since) {
        return repository.findByDateRange(since, LocalDate.now()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Evaluation> findByType(EvaluationType type) {
        return repository.findAll().stream()
                .filter(evaluation -> evaluation.getType() == type)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Evaluation> findMostRecentByConsultantAndType(Long consultantId, EvaluationType type) {
        return repository.findByConsultantIdAndType(consultantId, type).stream()
                .max(java.util.Comparator.comparing(EvaluationEntity::getEvaluationDate))
                .map(mapper::toDomain);
    }
}
