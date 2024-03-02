import java.util.Arrays;
import java.util.List;
/**
 * Provides a factory method for creating game instances based on specified game types. This class
 * centralizes the logic for instantiating game objects, facilitating easy addition and management of
 * new game types.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */

 public class GameFactory {
    public static List<String> gameTypes = Arrays.asList("Sliding Puzzle", "Dots & Boxes", "Quoridor");

    public static Game createGame(GameType game) {
        switch (game) {
            case SLIDING_PUZZLE:
                return new SlidingPuzzleGame();
            case DOTS_N_BOXES:
                return new DotsAndBoxesGame();
            case QUORIDOR:
                return new QuoridorGame();
            default:
                return null;
        }
    }
}



