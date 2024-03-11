import java.util.List;
import java.util.Scanner;
/**
 * Provides an abstract framework for game development, including lifecycle methods and game state management.
 * Subclasses implement game-specific logic and UI rendering. Common attributes like game name, player count,
 * and difficulty are also defined.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */

public abstract class Game {

    public String gameName;
    protected int numPlayers;
    private Board board;
    private Difficulty difficulty;

    boolean hasDifficulty;

    List<Object[]> Stats;


    /**
     * Initializes the game, setting up any necessary components or state before the game starts.
     */
    abstract void initialize();

    /**
     * Returns game name.
     */
    abstract String getGameName();

    /**
     * Initializes the board to a valid state.
     */
    abstract void createValidBoardState(Difficulty d);

    /**
     * Starts the game loop. This method should handle the main gameplay mechanics, including
     * player input, game state updates, and rendering.
     */
    abstract void start();

    /**
     * Ends the game, performing any necessary cleanup or state reset. This method might also
     * display the game's final state, scores, or a game over message.
     */
    abstract void end();

    /**
     * Resets the game to its initial state, allowing players to start over without restarting the application.
     */
    abstract void reset();

    /**
     * Checks the current state of the game to determine if the win condition has been met.
     * @return true if the game has been won, false otherwise.
     */
    abstract boolean checkWinCondition();

    /**
     * Updates the game state. This method is called within the game loop to process player actions,
     * update game entities, and handle game logic.
     */
    abstract void updateGameState(int toSwap);


    /**
     * Renders the game's current state to the terminal or console. This includes displaying the game board,
     * player scores, and any other relevant information.
     */
    abstract void render();

    abstract int getNumPlayers();


}
