import java.util.Arrays;

public class QuoridorGame extends Game{

    public final String gameName = "Quoridor";
    private Team[] teams;
    private BoardWithEdges board;
    private Difficulty difficulty = Difficulty.EASY; //Since this is a PvP game, it has no impact on the game.
    public QuoridorGame(){
        initialize();
    }

    @Override
    void initialize() {
        System.out.println("Welcome to Quoridor! The goal of this game is to get to your opponent's side \n" +
                "before your opponent gets to your side!");

        this.teams = ConsoleController.createTeams();

        System.out.println("Great! Lets now get the board dimensions, here we are referring to the # of boxes that can be drawn.");

        createValidBoardState(Difficulty.EASY); // GENERIC DIFFICULTY, DOES NOT IMPACT

        super.hasDifficulty = false;

        render();

        start();
    }

    @Override
    String getGameName() {
        return this.gameName;
    }

    @Override
    void createValidBoardState(Difficulty d) {
        int[] dims = ConsoleController.inputBoardDim();
        board = new BoardWithEdges(dims[0],dims[1]);
        this.board.numberBoardTiles();
    }

    @Override
    void start() {

    }

    @Override
    void end() {

    }

    @Override
    void reset() {

    }

    @Override
    boolean checkWinCondition() {
        return false;
    }

    @Override
    void updateGameState(int toSwap) {

    }

    @Override
    void render() {

    }

    @Override
    int getNumPlayers() {
        return 0;
    }
}
