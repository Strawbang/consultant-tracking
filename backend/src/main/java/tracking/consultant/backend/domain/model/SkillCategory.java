package tracking.consultant.backend.domain.model;

public enum SkillCategory {
    TECHNICAL("Technique"),
    SOFT_SKILLS("Compétences douces"),
    MANAGEMENT("Management"),
    METHODOLOGY("Méthodologie"),
    TOOLS("Outils"),
    LANGUAGES("Langues");

    private final String displayName;

    SkillCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
