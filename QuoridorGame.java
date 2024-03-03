import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.awt.Color;

public class QuoridorGame extends Game{

    public final String gameName = "Qoridor";
    private Team[] teams;
    private BoardWithEdges board;

    private List<Piece<Integer>> teamPawns;

    private Integer[] pawnPositions;
    private final Difficulty difficulty = Difficulty.EASY; //Since this is a PvP game, it has no impact on the game.
    public QuoridorGame(){
        initialize();
    }

    @Override
    void initialize() {
        System.out.println("Welcome to Quoridor! The goal of this game is to get to your opponent's side \n" +
                "before your opponent gets to your side!");

        this.teams = ConsoleController.createTeams();

        this.teamPawns = new ArrayList<>();

        pawnPositions = new Integer[teams.length];



        System.out.println("Great! Lets now get the board dimensions, here we are referring to the # of board tiles.");

        createValidBoardState(Difficulty.EASY); // GENERIC DIFFICULTY, DOES NOT IMPACT

        for (int i = 0; i < teams.length; i++) {
            teamPawns.add(new Piece<>(i));
            setPawnStartingPosition(i);
        }

        super.hasDifficulty = false;

        render();

        start();
    }

    void setPawnStartingPosition(int teamNumber) {
        if (teamNumber == 0){
            pawnPositions[teamNumber] = (board.getHeight() * board.getWidth()) -  (board.getWidth() / 2);
        }
        else if (teamNumber == 1){
            pawnPositions[teamNumber] = board.getWidth() -  (board.getWidth() / 2);
        }
        else if (teamNumber == 2){
            pawnPositions[teamNumber] = (board.getHeight() / 2) * board.getWidth() + 1;
        }
        else{
            pawnPositions[teamNumber] = (board.getHeight() / 2) * board.getWidth() + board.getWidth();
        }

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
        Queue<Player> queue = ConsoleController.loadPlayerQueue(teams);

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
        board.renderBoard(pawnPositions);
        System.out.println();
    }

    @Override
    int getNumPlayers() {
        return 0;
    }
}
