/**
 * Enumerates the difficulty levels for games, providing a standardized way of defining and accessing
 * game complexities.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public enum Difficulty {
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    HARD("HARD");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
