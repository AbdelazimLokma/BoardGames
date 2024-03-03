import java.util.*;
/**
 * Implements the game logic for Dots & Boxes, managing game setup, player turns, and scoring.
 * It extends the Game class, utilizing teams and a game board to facilitate player interactions,
 * edge drawing, and box completion for scoring, with methods to start, update, and end the game.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class DotsAndBoxesGame extends Game{

    public final String gameName = "Dots & Boxes";
    private Team[] teams;

    private Integer[] teamPoints;

    private BoardWithEdges board;
    private Difficulty difficulty = Difficulty.EASY; //Since this is a PvP game, it has no impact on the game.

    public DotsAndBoxesGame(){
        initialize();
    }

    @Override
    void initialize() {
        System.out.println("Welcome to Dots & Boxes, the goal of this game is to get as many points as possible by \n" +
                "connecting boxes together as a team!");

        this.teams = ConsoleController.createTeams();

        if(teams == null){
            System.out.println("You've decided to quit, returning you to the game menu, goodbye!");
            ConsoleController.chooseGame();
        }

        teamPoints = new Integer[teams.length];
        Arrays.fill(teamPoints, 0);

        System.out.println("Great! Lets now get the board dimensions, here we are referring to the # of boxes that can be drawn.");

        createValidBoardState(difficulty);

        super.hasDifficulty = false;

        render();

        start();
    }

    @Override
    String getGameName() {
        return gameName;
    }

    @Override
    void createValidBoardState(Difficulty d) {
        int[] dims = ConsoleController.inputBoardDim();
        board = new BoardWithEdges(dims[0],dims[1]);
        this.board.numberBoardTiles();
    }

    @Override
    void start() {
        boolean winCon = true;
        boolean saySomething = true;
        Queue<Player> queue = ConsoleController.loadPlayerQueue(teams);
        Tile [] flattenedBoard = board.flattenBoard();
        boolean playerScoredPoint = false;
        Player currPlayer = null;
        while (!checkWinCondition()){
            // take player action
            if (!playerScoredPoint){
                currPlayer = queue.poll();  //get a new player if a box was not drawn. keep the current player if they scored a point
                queue.add(currPlayer);
            }
            else{
                System.out.println( currPlayer.getpName() +", gets an extra turn for scoring a point!");
            }

            System.out.println( currPlayer.getpName() +", it is your turn, please choose a cell to draw on, then choose which edge you want to draw:");
            int[] cellCoord;
            int cellInput;
            List<Edge> edges;
            do {
                cellInput = Input.getIntInput(1, flattenedBoard.length, "Please choose the desired cell:");
                cellCoord = board.getTileCoords(flattenedBoard[cellInput-1]);
                edges = board.getUndrawnBoxEdges(cellCoord[0], cellCoord[1]);
                if (edges.isEmpty()){
                    System.out.println("ERROR - Please choose a cell corresponding to an incomplete box.");
                }
            }
            while(edges.isEmpty());

            List<String> edgeNames = board.getUndrawnBoxEdgeNames(cellCoord[0], cellCoord[1]);

            ConsoleController.displayOptions(edgeNames, "Please choose the number corresponding to the available edge to draw:");
            int edgeChoice = Input.getIntInput(1, edgeNames.size(), "");

            updateGameState(cellCoord, edgeChoice);
            // if box completed, assign point


            if (board.boxIsDrawn(cellCoord[0], cellCoord[1]) && board.checkIfAdjacentBoxCompleted(cellCoord[0], cellCoord[1], edges.get(edgeChoice-1))){
                teamPoints[currPlayer.getTeamNum()]+=2;
                System.out.println(currPlayer.getpName() + " just completed two boxes! Scoring two points for their team (total: "+ teamPoints[currPlayer.getTeamNum()] +")!");
                playerScoredPoint = true;
            }
            else if (board.boxIsDrawn(cellCoord[0], cellCoord[1])|| board.checkIfAdjacentBoxCompleted(cellCoord[0], cellCoord[1], edges.get(edgeChoice-1))){
                teamPoints[currPlayer.getTeamNum()]+=1;
                System.out.println(currPlayer.getpName() + " just completed a box! Scoring one point for their team (total: "+ teamPoints[currPlayer.getTeamNum()] +")!");
                playerScoredPoint = true;
            }
            else{
                System.out.println(currPlayer.getpName() + ", your edge has been drawn.");
                playerScoredPoint = false;
            }


            teams[currPlayer.getTeamNum()].incrementMovesMade();


            render();
            // repeat

        }
        end();
    }

    @Override
    void end() {
            ConsoleController.announceTeamScores(teams, teamPoints);
            ConsoleController.playAgain(teams, this);
    }

    @Override
    void reset() {
        for(Integer i: teamPoints){
            i = 0;
        }
    }

    @Override
    boolean checkWinCondition() {
        return board.boardIsDrawn();
    }

    void updateGameState(int[] cellCoord, int edge) {
        List<Edge> edges = board.getUndrawnBoxEdges(cellCoord[0], cellCoord[1]);
        edges.get(edge-1).isDrawn = true;
    }

    @Override
    void updateGameState(int toSwap) {

    }

    @Override
    void render() {
        board.renderBoard();
        System.out.println();
    }


    @Override
    int getNumPlayers() {
        super.numPlayers =  teams.length * teams[0].getPlayers().length;
        return numPlayers;
    }


}
