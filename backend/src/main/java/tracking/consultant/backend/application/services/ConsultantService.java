package tracking.consultant.backend.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tracking.consultant.backend.application.ports.ConsultantServicePort;
import tracking.consultant.backend.domain.model.*;
import tracking.consultant.backend.infrastructure.ports.ConsultantRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.SkillRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.ProjectRepositoryPort;
import tracking.consultant.backend.infrastructure.ports.EvaluationRepositoryPort;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultantService implements ConsultantServicePort {

    private final ConsultantRepositoryPort consultantRepository;
    private final SkillRepositoryPort skillRepository;
    private final ProjectRepositoryPort projectRepository;
    private final EvaluationRepositoryPort evaluationRepository;

    private void validateConsultant(Consultant consultant) {
        if (consultant.getFirstName() == null || consultant.getFirstName().trim().isEmpty()) {
            throw new IllegalStateException("Le prénom du consultant est obligatoire");
        }

        if (consultant.getLastName() == null || consultant.getLastName().trim().isEmpty()) {
            throw new IllegalStateException("Le nom du consultant est obligatoire");
        }

        if (consultant.getEmail() == null || consultant.getEmail().trim().isEmpty()) {
            throw new IllegalStateException("L'email du consultant est obligatoire");
        }

        if (!consultant.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalStateException("L'email du consultant n'est pas valide");
        }

        if (consultant.getHireDate() == null) {
            throw new IllegalStateException("La date d'embauche est obligatoire");
        }

        if (consultant.getHireDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("La date d'embauche ne peut pas être dans le futur");
        }
    }

    private void validateSkillAssignment(Consultant consultant, Skill skill, SkillLevel level) {
        if (level == null) {
            throw new IllegalStateException("Le niveau de compétence est obligatoire");
        }

        // Vérifier les prérequis
        Set<Skill> missingPrerequisites = skill.getPrerequisites().stream()
                .filter(prerequisite -> !consultant.getSkills().contains(prerequisite))
                .collect(Collectors.toSet());

        if (!missingPrerequisites.isEmpty()) {
            String missing = missingPrerequisites.stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
            throw new IllegalStateException("Le consultant n'a pas toutes les compétences requises : " + missing);
        }
    }

    private void validateProjectAssignment(Consultant consultant, Project project) {
        // Vérifier les dates du projet
        if (project.getStartDate() == null || project.getEndDate() == null) {
            throw new IllegalStateException("Les dates du projet sont obligatoires");
        }

        if (project.getStartDate().isAfter(project.getEndDate())) {
            throw new IllegalStateException("La date de début du projet ne peut pas être après la date de fin");
        }

        // Vérifier les chevauchements avec d'autres projets
        boolean hasProjectConflict = consultant.getProjects().stream()
            .anyMatch(existingProject -> 
                !existingProject.getId().equals(project.getId()) &&
                existingProject.getStartDate().isBefore(project.getEndDate()) &&
                existingProject.getEndDate().isAfter(project.getStartDate())
            );

        if (hasProjectConflict) {
            throw new IllegalStateException("Le consultant a déjà un projet sur cette période");
        }

        // Vérifier les compétences requises
        Set<Skill> missingSkills = project.getRequiredSkills().stream()
            .filter(skill -> !consultant.hasSkill(skill))
            .collect(Collectors.toSet());

        if (!missingSkills.isEmpty()) {
            String missing = missingSkills.stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
            throw new IllegalStateException("Le consultant n'a pas toutes les compétences requises : " + missing);
        }
    }

    public List<Consultant> findAll() {
        return consultantRepository.findAll();
    }

    public Optional<Consultant> findById(Long id) {
        return consultantRepository.findById(id);
    }

    public List<Consultant> findBySkill(Skill skill) {
        if (skill == null) {
            throw new IllegalArgumentException("La compétence recherchée est obligatoire");
        }
        return consultantRepository.findBySkill(skill);
    }

    public List<Consultant> findBySkillAndLevel(Skill skill, SkillLevel level) {
        if (skill == null) {
            throw new IllegalArgumentException("La compétence recherchée est obligatoire");
        }
        if (level == null) {
            throw new IllegalArgumentException("Le niveau de compétence est obligatoire");
        }
        return consultantRepository.findBySkillAndLevel(skill, level);
    }

    public List<Consultant> findByProject(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Le projet recherché est obligatoire");
        }
        return consultantRepository.findByProject(project);
    }

    public Consultant save(Consultant consultant) {
        validateConsultant(consultant);

        // Vérifier si l'email est déjà utilisé
        if (consultant.getId() == null) {
            boolean emailExists = consultantRepository.findByEmail(consultant.getEmail())
                .isPresent();
            if (emailExists) {
                throw new IllegalStateException("Un consultant avec cet email existe déjà");
            }
        }

        return consultantRepository.save(consultant);
    }

    public void deleteById(Long id) {
        Consultant consultant = consultantRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + id));

        // Vérifier si le consultant peut être supprimé
        if (!consultant.getProjects().isEmpty()) {
            throw new IllegalStateException("Le consultant ne peut pas être supprimé car il est affecté à des projets");
        }

        if (!consultant.getTrainings().isEmpty()) {
            throw new IllegalStateException("Le consultant ne peut pas être supprimé car il est inscrit à des formations");
        }

        List<Evaluation> evaluations = evaluationRepository.findByConsultantId(id);
        if (!evaluations.isEmpty()) {
            throw new IllegalStateException("Le consultant ne peut pas être supprimé car il a des évaluations");
        }

        consultantRepository.delete(consultant);
    }

    public void addSkill(Long consultantId, Long skillId, SkillLevel level) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + consultantId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id : " + skillId));

        validateSkillAssignment(consultant, skill, level);
        consultant.addSkill(skill, level);
        consultantRepository.save(consultant);
    }

    public void removeSkill(Long consultantId, Long skillId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + consultantId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id : " + skillId));

        // Vérifier si la compétence est utilisée dans des projets en cours
        boolean skillUsedInProjects = consultant.getProjects().stream()
            .filter(project -> project.getEndDate().isAfter(LocalDate.now()))
            .anyMatch(project -> project.getRequiredSkills().contains(skill));

        if (skillUsedInProjects) {
            throw new IllegalStateException("Cette compétence est requise pour un projet en cours");
        }

        // Vérifier si d'autres compétences dépendent de celle-ci
        Set<Skill> dependentSkills = consultant.getSkills().stream()
            .filter(s -> s.getPrerequisites().contains(skill))
            .collect(Collectors.toSet());

        if (!dependentSkills.isEmpty()) {
            String dependent = dependentSkills.stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));
            throw new IllegalStateException("Cette compétence est un prérequis pour d'autres compétences : " + dependent);
        }

        consultant.removeSkill(skill);
        consultantRepository.save(consultant);
    }

    public void updateSkillLevel(Long consultantId, Long skillId, SkillLevel level) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + consultantId));
        
        Skill skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new IllegalArgumentException("Compétence non trouvée avec l'id : " + skillId));

        if (!consultant.hasSkill(skill)) {
            throw new IllegalStateException("Le consultant ne possède pas cette compétence");
        }

        SkillLevel currentLevel = consultant.getSkillLevel(skill);
        if (level.ordinal() < currentLevel.ordinal()) {
            throw new IllegalStateException("Le niveau de compétence ne peut pas être diminué directement");
        }

        consultant.updateSkillLevel(skill, level);
        consultantRepository.save(consultant);
    }

    public void addProject(Long consultantId, Long projectId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + consultantId));
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id : " + projectId));

        validateProjectAssignment(consultant, project);
        consultant.addProject(project);
        consultantRepository.save(consultant);
    }

    public void removeProject(Long consultantId, Long projectId) {
        Consultant consultant = consultantRepository.findById(consultantId)
            .orElseThrow(() -> new IllegalArgumentException("Consultant non trouvé avec l'id : " + consultantId));
        
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Projet non trouvé avec l'id : " + projectId));

        if (project.getStartDate().isBefore(LocalDate.now()) && 
            project.getEndDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Le consultant ne peut pas être retiré d'un projet en cours");
        }

        consultant.removeProject(project);
        consultantRepository.save(consultant);
    }

    @Override
    public void removeSkillFromConsultant(Long consultantId, Long skillId) {
        Optional<Consultant> consultantOptional = consultantRepository.findById(consultantId);
        Optional<Skill> skillOptional = skillRepository.findById(skillId);

        if (consultantOptional.isEmpty()) {
            throw new IllegalStateException("Consultant not found with ID: " + consultantId);
        }

        if (skillOptional.isEmpty()) {
            throw new IllegalStateException("Skill not found with ID: " + skillId);
        }

        Consultant consultant = consultantOptional.get();
        Skill skill = skillOptional.get();

        consultant.removeSkill(skill);
        consultantRepository.save(consultant);
    }

    @Override
    public Consultant createConsultant(Consultant consultant) {
        validateConsultant(consultant);
        return consultantRepository.save(consultant);
    }

    @Override
    public Optional<Consultant> getConsultantById(Long id) {
        return consultantRepository.findById(id);
    }

    @Override
    public List<Consultant> getAllConsultants() {
        return consultantRepository.findAll();
    }

    @Override
    public List<Consultant> getConsultantsBySkill(Skill skill) {
        return consultantRepository.findBySkill(skill);
    }

    @Override
    public List<Consultant> getConsultantsByProject(Project project) {
        return consultantRepository.findByProject(project);
    }

    @Override
    public List<Consultant> getAvailableConsultants() {
        return consultantRepository.findAvailableConsultants();
    }

    @Override
    public List<Consultant> getConsultantsBySkillLevel(Skill skill, int minimumLevel) {
        SkillLevel minimumSkillLevel;
        switch (minimumLevel) {
            case 1:
                minimumSkillLevel = SkillLevel.BEGINNER;
                break;
            case 2:
                minimumSkillLevel = SkillLevel.INTERMEDIATE;
                break;
            case 3:
                minimumSkillLevel = SkillLevel.ADVANCED;
                break;
            case 4:
                minimumSkillLevel = SkillLevel.EXPERT;
                break;
            default:
                throw new IllegalStateException("Skill level must be between 1 and 4");
        }
        return consultantRepository.findBySkillAndLevel(skill, minimumSkillLevel);
    }

    @Override
    public Consultant updateConsultant(Long id, Consultant consultant) {
        Optional<Consultant> existingConsultant = consultantRepository.findById(id);
        if (existingConsultant.isEmpty()) {
            throw new IllegalStateException("Consultant not found with ID: " + id);
        }
        
        validateConsultant(consultant);
        consultant.setId(id);
        return consultantRepository.save(consultant);
    }

    @Override
    public void deleteConsultant(Long id) {
        Optional<Consultant> consultant = consultantRepository.findById(id);
        if (consultant.isEmpty()) {
            throw new IllegalStateException("Consultant not found with ID: " + id);
        }
        consultantRepository.delete(consultant.get());
    }

    @Override
    public void addSkillToConsultant(Long consultantId, Long skillId, SkillLevel level) {
        Optional<Consultant> consultantOptional = consultantRepository.findById(consultantId);
        Optional<Skill> skillOptional = skillRepository.findById(skillId);

        if (consultantOptional.isEmpty()) {
            throw new IllegalStateException("Consultant not found with ID: " + consultantId);
        }

        if (skillOptional.isEmpty()) {
            throw new IllegalStateException("Skill not found with ID: " + skillId);
        }

        Consultant consultant = consultantOptional.get();
        Skill skill = skillOptional.get();

        consultant.addSkill(skill, level);
        consultantRepository.save(consultant);
    }

    @Override
    public void updateConsultantSkillLevel(Long consultantId, Long skillId, SkillLevel newLevel) {
        Optional<Consultant> consultantOptional = consultantRepository.findById(consultantId);
        Optional<Skill> skillOptional = skillRepository.findById(skillId);

        if (consultantOptional.isEmpty()) {
            throw new IllegalStateException("Consultant not found with ID: " + consultantId);
        }

        if (skillOptional.isEmpty()) {
            throw new IllegalStateException("Skill not found with ID: " + skillId);
        }

        Consultant consultant = consultantOptional.get();
        Skill skill = skillOptional.get();

        consultant.updateSkillLevel(skill, newLevel);
        consultantRepository.save(consultant);
    }
}
