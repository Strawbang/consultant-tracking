package tracking.consultant.backend.domain.model;

public enum SkillLevel {
    BEGINNER(1),
    INTERMEDIATE(2),
    ADVANCED(3),
    EXPERT(4);

    private final int value;

    SkillLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isHigherThan(SkillLevel other) {
        return this.value > other.value;
    }

    public static SkillLevel fromValue(int value) {
        return switch (value) {
            case 1 -> BEGINNER;
            case 2 -> INTERMEDIATE;
            case 3 -> ADVANCED;
            case 4 -> EXPERT;
            default -> BEGINNER;
        };
    }
}
