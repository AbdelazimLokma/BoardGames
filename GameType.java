/**
 * Defines the types of games available in the application, each associated with a specific display name
 * for user-friendly identification.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public enum GameType {
    SLIDING_PUZZLE("Sliding Puzzle"),
    DOTS_N_BOXES("Dots & Boxes"),

    QUORIDOR("Quoridor");
//    CHESS("Chess"),
//    CHECKERS("Checkers");

    private final String displayName;

    GameType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
