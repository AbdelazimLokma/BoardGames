import java.util.*;
/**
 * Implements a Sliding Puzzle game extending the abstract Game class, focusing on puzzle-solving where players rearrange tiles to achieve a specific configuration.
 * It manages game initialization, gameplay flow, rendering of the game state, and checks for win conditions based on tile positions.
 * The game's difficulty affects the initial shuffle of tiles, and players interact through console input to move tiles until the puzzle is solved.
 * Supports single-player mode with customizable board sizes and difficulty levels.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public class SlidingPuzzleGame extends Game{

    final static int minSize = 2;
    final static int maxSize = 6;
    private final static int numPlayers = 1;
    private final static String gameName = "Sliding Puzzle";
    private Player player;
    private Board board;
    private Tile emptyTile;
    private Difficulty difficulty;


    public SlidingPuzzleGame(){
        initialize();
    }

    @Override
    public String getGameName(){
        return this.gameName;
    }

    @Override
    void initialize() {

        super.hasDifficulty= true;

        this.player = ConsoleController.createPlayer();

        System.out.println("Thank you, " + player.getpName() + ", please set your board size:");

        int[] boardDimensions = ConsoleController.inputBoardDim();
        board = new Board(boardDimensions[0], boardDimensions[1]);

        reset();

        createValidBoardState(ConsoleController.chooseDifficulty());

        System.out.println("Your sliding puzzle is ready!");

        start();
    }

    @Override
    void start() {
        int choice= 0;
        boolean saySomething = true;
        while(!checkWinCondition()){
            List<Integer> availableSwaps = convertTilesToInts(board.findAdjacentTiles(emptyTile));
            Collections.sort(availableSwaps);
            boolean inputCondition = true;
            render();
            String prompt = player.getpName() +", which tile do you want to slide to the empty space?";
            choice = Input.getIntInput(availableSwaps.get(0), availableSwaps.get(availableSwaps.size()-1), prompt);
            updateGameState(choice);
        }
        end();
    }


    @Override
    void end() {
        System.out.println("Congrats! You've solved the Sliding Puzzle");
        ConsoleController.playAgain(player, this);
    }


    @Override
    void reset() {
        this.board.numberBoardTiles();
        emptyTile = board.getTile( board.getHeight()-1, board.getWidth()-1);
        emptyTile.setPiece( new Piece<Character>(' '));
    }

    @Override
    boolean checkWinCondition() {

        Tile[] flattenedArray = board.flattenBoard();

        for(int i = 1; i < flattenedArray.length - 1; i++){ //we should not reach the end of the board
            if(flattenedArray[i-1].equals(emptyTile)||flattenedArray[i].equals(emptyTile)){
                return false; //empty tile is in the current two tiles, board is not sorted
            }
            else if((Integer) flattenedArray[i-1].getPiece().getType() > (Integer) flattenedArray[i].getPiece().getType()){
                return false;
            }
        }
        player.setNumWins(player.getNumWins()+1);;
        return true;
    }

    @Override
    void updateGameState(int toSwap) {
        List<Tile> neighbours = board.findAdjacentTiles(emptyTile);
        for(Tile t: neighbours){
            if ((Integer) t.getPiece().getType() == toSwap){ //toSwap must be in the neighbors list
                swapEmptyPiece(t);
            }
        }
    }

    @Override
    void render() {
        board.renderBoard();
    }


    void createValidBoardState(Difficulty difficulty){
        int numSwaps;

        if (difficulty.equals(Difficulty.EASY)){
            numSwaps = 3;
        }
        else if (difficulty.equals(Difficulty.MEDIUM)){
            numSwaps = 6;
        }
        else{
            numSwaps = 10;
        }

        Random random = new Random();
        for (int i=numSwaps; i > 0; i--){
            List<Tile> neighbours = board.findAdjacentTiles(emptyTile);
            Tile randomNeighbor = neighbours.get(random.nextInt(neighbours.size()));
            swapEmptyPiece(randomNeighbor);
        }

    }

    private int[] getEmptyTileCoords(){
        return board.getTileCoords(emptyTile);
    }

    private void swapEmptyPiece(Tile t2){
        Piece placeHolder = emptyTile.getPiece();
        emptyTile.setPiece(t2.getPiece());
        t2.setPiece(placeHolder);
        emptyTile = t2;
    }

    private List<Integer> convertTilesToInts(List<Tile> list){
        List<Integer> ret= new ArrayList<>();
        for(Tile t: list){
            ret.add((Integer) t.getPiece().getType());
        }
        return ret;
    }
    @Override
    public int getNumPlayers() {
        return numPlayers;
    }
}
