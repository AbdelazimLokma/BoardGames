import java.util.*;

/**
 * Implements the game logic for Quoridor, managing game setup, player turns, etc.
 * It extends the Game class, utilizing teams and a game board to facilitate player interactions,
 * edge drawing, with methods to start, update, and end the game.
 *
 */

public class QuoridorGame extends Game{

    public final String gameName = "Quoridor";
    private Team[] teams;
    private BoardWithEdges board;

    private List<Piece<Integer>> teamPawns;

    private int[] wallsPerTeam;

    private Integer[] pawnPositions;
    private final Difficulty difficulty = Difficulty.EASY; //Since this is a PvP game, it has no impact on the game.
    public QuoridorGame(){
        initialize();
    }

    @Override
    /**
     * Initializes and starts the game.
     *
     */
    void initialize() {
        System.out.println("Welcome to Quoridor! The goal of this game is to get to your opponent's side\n" +
                "before your opponent gets to your side!");

        this.teams = ConsoleController.createTeams();

        this.numPlayers = teams[0].getPlayers().length * teams.length;

        this.teamPawns = new ArrayList<>();

        for (Team team: teams){
            team.setStatNames("Number of offensive moves made (pawn movements)", "Number of defensive moves (wall placements)");
        }

        setWallsPerTeam();

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

    /**
     * Sets the pawn starting positions based on the team number
     * @param teamNumber
     */
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
        int[] dims = ConsoleController.inputBoardDim(this);
        board = new BoardWithEdges(dims[0],dims[1]);
        this.board.numberBoardTiles();
        board.fillBorderWalls();
    }

    @Override
    /**
     * Implements the main Quoridor game loop
     */
    void start() {
        Random firstStart = new Random();
        int randomInt = firstStart.nextInt(teams.length);

        String[] currTeamInfo = ConsoleController.getTeamInfo(randomInt, teams);
        System.out.println("Team: " + currTeamInfo[1] + " (displayed on the board with the color: " + Utility.colorString(currTeamInfo[0], randomInt) +  ")\nhas won the draw, they get to go first!");

        Queue<Player> queue = ConsoleController.loadPlayerQueue(teams, randomInt);


        while (true){
            //draw player from queue, take input
            Player p = queue.poll();

            System.out.println("Player " + p.getpName() + ", of team: " +Utility.colorString(teams[p.getTeamNum()].getTeamName(), p.getTeamNum()) +  ", it is your turn!");

            int playerChoice = movePawnOrPlaceWall(p);

            updateGameState(playerChoice, p);

            render();
            if (checkWinCondition(p.getTeamNum())) {
                end(p);
                break;
            }
            //add to back of queue
            queue.add(p);


        }



    }

    @Override
    void end() {
    }

    void end(Player p){
        System.out.println("Congratulations team " + Utility.colorString(teams[p.getTeamNum()].getTeamName(), p.getTeamNum()) + " you have won!");
        ConsoleController.playAgain(teams, this);
    }

    @Override
    void reset() {
        for (int i = 0; i < teams.length; i++) {

            setPawnStartingPosition(i);
        }
        setWallsPerTeam();

    }

    @Override
    boolean checkWinCondition() {
        return false;
    }

    /**
     * Overrides the win condition method because we want to check
     * which team reached their goal
     * @param teamNum
     */
    boolean checkWinCondition(int teamNum) {
        int pos = Utility.convert1Dto2D(pawnPositions[teamNum], board.getWidth())[0];
        if (teamNum == 0 && pos == 0) {
            return true;
        }
        if (teamNum == 1 && pos == board.getHeight() - 1) {
            return true;
        }

        pos = Utility.convert1Dto2D(pawnPositions[teamNum], board.getWidth())[1];
        if (teamNum == 2 && pos == board.getWidth() - 1) {
            return true;
        }
        return teamNum == 3 && pos == 0;
    }

    @Override
    void updateGameState(int num){};
    void updateGameState(int playerChoice, Player p) {
        if(playerChoice == 1){ //player chooses to move pawn
            //TODO: pawn movement mechanic and player pawn movement choice mechanic
            movePawn(p);
        }
        else{
            getWallInfo(p);
        }//process - process player input
    }

    @Override
    void render() {
        board.renderBoard(pawnPositions, null);
        System.out.println();
    }

    void render(List<Integer> validTiles) {
        board.renderBoard(pawnPositions, validTiles);
        System.out.println();
    }

    @Override
    int getNumPlayers() {
        return 0;
    }

    /**
     * gives the user an option to either move their pawn or place a wall
     * @param p
     */
    private int movePawnOrPlaceWall (Player p){
        List<String> options = new ArrayList<>();
        options.add("Move Pawn");
        if (wallsPerTeam[p.getTeamNum()] > 0){
            options.add("Add Wall, (walls remaining: "+ wallsPerTeam[p.getTeamNum()]+").");
            ConsoleController.displayOptions(options, "Player " + p.getpName() + ", would you like to move your pawn or place a wall");
            return Input.getIntInput(1, 2, "Please input the number corresponding to the desired option:");
        }
        else{
             System.out.println("Player " + p.getpName() + ", your team is out of walls, your only option is to move the pawn.");
             return 1;
        }

    }

    /**
     * asks the user where they want to place their wall
     * @param p
     */
    private void getWallInfo(Player p){

        int[] tileCoord = new int[0];
        int[] edgeValidity = new int[0];
        int edgeIndex = 0;
        int direction = 0;
        boolean exitMethod = false;
        Edge tileEdge = null;
        Edge adjEdge = null;

        do {
            if(!checkIfPawnPathsBlocked ()){
                tileEdge.isDrawn = false;
                adjEdge.isDrawn = false;

            }
            int tileNum1D = Input.getIntInput(0, board.flattenBoard().length, "Please select which tile to add a wall to\n" +
                    "or select 0 to move your pawn instead:");

            if(tileNum1D == 0){
                movePawn(p);
                exitMethod = true;
            }
            else{
                tileCoord = Utility.convert1Dto2D(tileNum1D, board.getWidth());
                edgeValidity = getValidEdges(tileCoord[0], tileCoord[1]);
                List<String> validEdges = new ArrayList<>();

                for (int i = 0 ; i < 4; i++){
                    if (edgeValidity[i] != 0){
                        validEdges.add(board.getEdgeName(i));
                    }
                }


                ConsoleController.displayOptions(validEdges,"Now, please select which side of the tile to place an edge on:");
                edgeIndex = Input.getIntInput(1, validEdges.size(), "Choose a number: ");
                edgeIndex--;

                edgeIndex = board.getIndexFromDirection(validEdges.get(edgeIndex));


                if(edgeValidity[edgeIndex] != 1  && edgeValidity[edgeIndex] != 2){
                    List<String> directions= new ArrayList<>();

                    if (edgeIndex == 0 ) {
                        directions.add("left");
                        directions.add("right");
                    }
                    else if ( edgeIndex == 2){
                        directions.add("right");
                        directions.add("left");
                    }
                    else if(edgeIndex == 3){
                        directions.add("down");
                        directions.add("up");
                    }
                    else{
                        directions.add("up");
                        directions.add("down");
                    }

                    ConsoleController.displayOptions(directions,"Which direction do you want your wall to face?");
                    direction = Input.getIntInput(1, 2, "Choose a number: ");

                }

                if (edgeValidity[edgeIndex] == 1 || edgeValidity[edgeIndex] == 2){ //is the validity of this edge 1 or 2
                    board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
                    tileEdge = board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex];
                    adjEdge =  placeAdjacentEdge(tileCoord, edgeIndex, edgeValidity[edgeIndex]);
                }
                else{
                    board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
                    tileEdge = board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex];
                    if(edgeIndex == 2 && direction == 1){
                        adjEdge = placeAdjacentEdge(tileCoord, edgeIndex, 2);
                    } else if (edgeIndex == 2 && direction == 2) {
                        adjEdge = placeAdjacentEdge(tileCoord, edgeIndex, 1);
                    }
                    else{
                        adjEdge = placeAdjacentEdge(tileCoord, edgeIndex, direction);
                    }
                }


                if(!checkIfPawnPathsBlocked ()){
                    System.out.println("You cannot place a wall that blocks another pawns path to their objective, please try again\n" +
                            "or input 0 to move your pawn instead");
                }
            }

        }
        while (!checkIfPawnPathsBlocked ());

        if (!exitMethod){
            board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;

            placeWall(tileCoord, edgeValidity, edgeIndex, direction);


            System.out.println("Wall has been placed!");
            wallsPerTeam[p.getTeamNum()] --;
            teams[p.getTeamNum()].incrementStat2();
        }

        
    }

    /**
     * places wall based on the user's decision
     * @param tileCoord
     * @param edgeValidity
     * @param edgeIndex
     * @param direction
     */
    private void placeWall(int[] tileCoord, int[] edgeValidity, int edgeIndex, int direction) {
        if (edgeValidity[edgeIndex] == 1 || edgeValidity[edgeIndex] == 2){ //is the validity of this edge 1 or 2
            board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
            placeAdjacentEdge(tileCoord, edgeIndex, edgeValidity[edgeIndex]);
        }
        else{
            if(edgeIndex == 2 && direction == 1){
                placeAdjacentEdge(tileCoord, edgeIndex, 2);
            } else if (edgeIndex == 2 && direction == 2) {
                placeAdjacentEdge(tileCoord, edgeIndex, 1);
            }
            else{
                placeAdjacentEdge(tileCoord, edgeIndex, direction);
            }
        }
    }

    /**
     * places the necessary adjacent edge to a wall since a wall should
     * cover 2 edges
     * @param curBoxCoord
     * @param edgeIndx
     * @param direction
     * @return
     */
    private Edge placeAdjacentEdge(int[] curBoxCoord, int edgeIndx, int direction){
        if (edgeIndx == 0){
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]-1)[0].isDrawn = true;
                return  board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]-1)[0];
            }
            else{
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[0].isDrawn = true;
                return  board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[0];
            }
        }
        else if (edgeIndx == 1){
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1])[1].isDrawn = true;
                return board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1])[1];
            }
            else{
                board.getBoxEdges(curBoxCoord[0]+1, curBoxCoord[1])[1].isDrawn = true;
                return board.getBoxEdges(curBoxCoord[0]+1, curBoxCoord[1])[1];
            }
        }
        else if (edgeIndx == 2){
            if (direction == 1){ //place to the left (left in this case means relative to the board)
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]-1)[2].isDrawn = true;
                return  board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]-1)[2];
            }
            else{
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[2].isDrawn = true;
                return board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[2];
            }
        }
        else{
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0] + 1, curBoxCoord[1])[3].isDrawn = true;
                return board.getBoxEdges(curBoxCoord[0] + 1, curBoxCoord[1])[3];
            }
            else{
                board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1])[3].isDrawn = true;
                return  board.getBoxEdges(curBoxCoord[0] - 1, curBoxCoord[1])[3];
            }
        }
    }

    /**
     * determines which edges are valid to place a wall on
     * @param x
     * @param y
     * @return
     */
    private int[] getValidEdges(int x, int y){

        List<Edge> undrawnEdges =  board.getUndrawnBoxEdges(x,y, true);
        int[] validEdges =  new int[4];
        //returns an array of numbers, each number represents how many ways we can place a wall on a certain edge
        // 3 - wall can be placed extending left or right
        // 2 - wall can be places extending right only
        // 1 - wall can be places extending left only
        // 0 - wall cannot be placed at all on this edge


        for (int i = 0; i< undrawnEdges.size(); i++){
            if (undrawnEdges.get(i).getType().equals("up")){ //check top edges
                if (y == 0){
                    if (!board.getBoxEdges(x,y+1)[0].isDrawn){
                        validEdges[0] = 2;
                    }
                } else if (y == board.getWidth()-1) {
                    if (!board.getBoxEdges(x,y-1)[0].isDrawn){
                        validEdges[0] = 1;
                    }
                }
                else{ //central tile, check the top edge of both adjacent tiles
                    if (!board.getBoxEdges(x,y-1)[0].isDrawn && !board.getBoxEdges(x,y+1)[0].isDrawn){
                        validEdges[0] = 3;
                    }
                    else if (!board.getBoxEdges(x,y-1)[0].isDrawn){
                        validEdges[0] = 1;
                    }
                    else if (!board.getBoxEdges(x,y+1)[0].isDrawn) {
                        validEdges[0] = 2;
                    }
                }
            }
            else if (undrawnEdges.get(i).getType().equals("right")){
                if (x == 0){ //top row tile, only check row below
                    if (!board.getBoxEdges(x+1,y)[1].isDrawn){
                        validEdges[1] = 2;
                    }
                } else if (x == board.getHeight()-1) { //bottom row tile, only check row above
                    if (!board.getBoxEdges(x-1,y)[1].isDrawn){
                        validEdges[1] = 1;
                    }
                }
                else{ //central tile, check the top edge of both adjacent tiles
                    if (!board.getBoxEdges(x-1,y)[1].isDrawn && !board.getBoxEdges(x+1,y)[1].isDrawn){
                        validEdges[1] = 3;
                    }
                    else if (!board.getBoxEdges(x-1,y)[1].isDrawn){
                        validEdges[1] = 1;
                    }
                    else if (!board.getBoxEdges(x+1,y)[1].isDrawn) {
                        validEdges[1] = 2;
                    }
                }

            }
            else if (undrawnEdges.get(i).getType().equals("down")){ //check bottom edges
                if (y == 0){
                    if (!board.getBoxEdges(x,y+1)[2].isDrawn){
                        validEdges[2] = 2;
                    }
                } else if (y == board.getWidth()-1) {
                    if (!board.getBoxEdges(x,y-1)[2].isDrawn){
                        validEdges[2] = 1; //change to 2 if left is relative to edge and not board
                    }
                }
                else{ //central tile, check the top edge of both adjacent tiles
                    if (!board.getBoxEdges(x,y-1)[2].isDrawn && !board.getBoxEdges(x,y+1)[2].isDrawn){
                        validEdges[2] = 3;
                    }
                    else if (!board.getBoxEdges(x,y-1)[2].isDrawn){
                        validEdges[2] = 1;
                    }
                    else if (!board.getBoxEdges(x,y+1)[2].isDrawn) {
                        validEdges[2] = 2;
                    }
                }
            }
            else {
                if (x == 0){ //top row tile, only check row below
                    if (!board.getBoxEdges(x+1,y)[3].isDrawn){
                        validEdges[3] = 1;
                    }
                } else if (x == board.getHeight()-1) { //bottom row tile, only check row above
                    if (!board.getBoxEdges(x-1,y)[3].isDrawn){
                        validEdges[3] = 2;
                    }
                }
                else{ //central tile, check the top edge of both adjacent tiles
                    if (!board.getBoxEdges(x-1,y)[3].isDrawn && !board.getBoxEdges(x+1,y)[3].isDrawn){
                        validEdges[3] = 3;
                    }
                    else if (!board.getBoxEdges(x-1,y)[3].isDrawn){
                        validEdges[3] = 2;
                    }
                    else if (!board.getBoxEdges(x+1,y)[3].isDrawn) {
                        validEdges[3] = 1;
                    }
                }

            }
        }
        return validEdges;
    }


    private void setWallsPerTeam(){
        int numTotalWalls = 20;
        int numTeams = teams.length;
        this.wallsPerTeam = new int[teams.length];

        if (numTeams == 3){
            Arrays.fill(wallsPerTeam, 7);
        }
        else{
            Arrays.fill(wallsPerTeam, numTotalWalls/numTeams);
        }
    }

    private void movePawn(Player p){
        int pawnPos = pawnPositions[p.getTeamNum()];
        List<Integer> validMoveTiles = checkIfBasicMovesPossible(pawnPos, p.getTeamNum());
        System.out.println("The board will be displayed with valid tiles in " + Utility.colorString("magenta.", -1)
                +"\nPlease choose from these tiles.");
        render(validMoveTiles);
        int choice = Input.getIntInput(validMoveTiles, "Please enter the tile # you want to move to");
        pawnPositions[p.getTeamNum()] = choice;
        System.out.println("Your pawn has succesfully moved to tile: "+ choice);
        teams[p.getTeamNum()].incrementStat1();
    }

    /**
     * Checks if the basic pawn movements are possible(no jumps)
     * @param pawnPos
     * @param teamNum
     * @return
     */
    private List<Integer> checkIfBasicMovesPossible(int pawnPos, int teamNum){
        List<Integer> validMoveTiles = new ArrayList<>();
        //first check if movement options are out of bounds
        int[] pawnCoord = Utility.convert1Dto2D(pawnPos, board.getWidth());
        int[][] pawnPositions2D = get2DPawnPositions();
        Edge[] edges = board.getBoxEdges(pawnCoord[0], pawnCoord[1]);
        List<int[]> adjPawnCoordDir = getAdjPawnCoordsAndDirection(pawnCoord, pawnPositions2D);
        if (pawnCoord[0] != 0 && !edges[0].isDrawn && !isDirectionBlockedByPawn(0, adjPawnCoordDir)){ //checking if above tile is reachable, could be out of bounds, blocked by wall or enemy
            int tileNum = Utility.convert2Dto1D(pawnCoord[0]-1, pawnCoord[1], board.getWidth());
            validMoveTiles.add(tileNum);
        }
        if (pawnCoord[0] != board.getHeight()-1 && !edges[2].isDrawn && !isDirectionBlockedByPawn(2, adjPawnCoordDir)){ //checking if below tile is reachable, could be out of bounds, blocked by wall or enemy
            int tileNum = Utility.convert2Dto1D(pawnCoord[0]+1, pawnCoord[1], board.getWidth());
            validMoveTiles.add(tileNum);
        }

        if (pawnCoord[1] != 0 && !edges[3].isDrawn && !isDirectionBlockedByPawn(3, adjPawnCoordDir)){ //checking if left tile is reachable, could be out of bounds, blocked by wall or enemy
            int tileNum = Utility.convert2Dto1D(pawnCoord[0], pawnCoord[1]-1, board.getWidth());
            validMoveTiles.add(tileNum);
        }
        if (pawnCoord[1] != board.getWidth()-1 && !edges[1].isDrawn && !isDirectionBlockedByPawn(1, adjPawnCoordDir)){ //checking if below tile is reachable, could be out of bounds, blocked by wall or enemy
            int tileNum = Utility.convert2Dto1D(pawnCoord[0], pawnCoord[1]+1, board.getWidth());
            validMoveTiles.add(tileNum);
        }

        List<Integer> specialMoves = new ArrayList<>();

        if (adjPawnCoordDir != null) {
            specialMoves = getSpecialValidTiles(adjPawnCoordDir, pawnPos, teamNum);
        }

        for (Integer move : specialMoves) {
            if (!validMoveTiles.contains(move)) {
                validMoveTiles.add(move);
            }
        }

        return validMoveTiles;
    }

    /**
     * Checks which jumps are possible
     * @param adjPawnCoordDir
     * @param pawnPos
     * @param teamNum
     * @return
     */
    private List<Integer> getSpecialValidTiles(List<int[]> adjPawnCoordDir, int pawnPos, int teamNum) {
        List<Integer> specialValidTiles = new ArrayList<>();
        // Get pawn coordinates
        int[] pawnCoord = Utility.convert1Dto2D(pawnPos, board.getWidth());

        // Check each adjacent pawn for jumping possibility
        for (int[] adjPawn : adjPawnCoordDir) {
            int adjPawnX = adjPawn[0];
            int adjPawnY = adjPawn[1];
            int direction = adjPawn[2];

            Edge[] edges = board.getBoxEdges(adjPawnX, adjPawnY);


            if (teamNum == 1 && adjPawnX > pawnCoord[0]) {
                int destX = adjPawnX + 1;
                int destY = pawnCoord[1];
                int tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                if (!(tileNum > board.getHeight() * board.getWidth())) {
                    if (!edges[2].isDrawn) {
                        specialValidTiles.add(tileNum);
                    }
                    else {
                        if (!edges[1].isDrawn) {
                            destX = pawnCoord[0] + 1;
                            destY = pawnCoord[1] + 1;
                            tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                            if (tileNum <= board.getHeight() * board.getWidth()) {
                                specialValidTiles.add(tileNum);
                            }
                        }
                        if (!edges[3].isDrawn) {
                            destX = pawnCoord[0] + 1;
                            destY = pawnCoord[1] - 1;
                            tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                            if (tileNum <= board.getHeight() * board.getWidth()) {
                                specialValidTiles.add(tileNum);
                            }
                        }
                    }
                }
            }
            else if (teamNum == 0 && adjPawnX < pawnCoord[0]) {
                int destX = adjPawnX - 1;
                int destY = pawnCoord[1];
                int tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                if (!(tileNum < 1)) {
                    if (!edges[0].isDrawn) {
                        specialValidTiles.add(tileNum);
                    }
                    else {
                        if (!edges[1].isDrawn) {
                            destX = pawnCoord[0] - 1;
                            destY = pawnCoord[1] + 1;
                            tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                            if (tileNum <= board.getHeight() * board.getWidth()) {
                                specialValidTiles.add(tileNum);
                            }
                        }
                        if (!edges[3].isDrawn) {
                            destX = pawnCoord[0] - 1;
                            destY = pawnCoord[1] - 1;
                            tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                            if (tileNum <= board.getHeight() * board.getWidth()) {
                                specialValidTiles.add(tileNum);
                            }
                        }
                    }
                }
            }
            else if (teamNum == 2 && adjPawnY > pawnCoord[1]) {
                int destX = pawnCoord[0];
                int destY = adjPawnY + 1;
                int tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                if (!(destY >= board.getWidth())) {
                    if (!edges[1].isDrawn) {
                        specialValidTiles.add(tileNum);
                    }
                    else {
                        if (!edges[0].isDrawn) {
                            destX = pawnCoord[0] - 1;
                            destY = pawnCoord[1] + 1;
                            if (destX >= 0) {
                                tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                                specialValidTiles.add(tileNum);
                            }
                        }
                        if (!edges[2].isDrawn) {
                            destX = pawnCoord[0] + 1;
                            destY = pawnCoord[1] + 1;
                            if (destX < board.getHeight()) {
                                tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                                specialValidTiles.add(tileNum);
                            }
                        }
                    }
                }
            }
            else if (teamNum == 3 && adjPawnY < pawnCoord[1]) {
                int destX = pawnCoord[0];
                int destY = adjPawnY - 1;
                int tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                if (!(destY < 0)) {
                    if (!edges[3].isDrawn) {
                        specialValidTiles.add(tileNum);
                    }
                    else {
                        if (!edges[0].isDrawn) {
                            destX = pawnCoord[0] - 1;
                            destY = pawnCoord[1] - 1;
                            if (destX >= 0) {
                                tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                                specialValidTiles.add(tileNum);
                            }
                        }
                        if (!edges[2].isDrawn) {
                            destX = pawnCoord[0] + 1;
                            destY = pawnCoord[1] - 1;
                            if (destX < board.getHeight()) {
                                tileNum = Utility.convert2Dto1D(destX, destY, board.getWidth());
                                specialValidTiles.add(tileNum);
                            }
                        }
                    }
                }
            }
        }
        return specialValidTiles;
    }


    private int[][] get2DPawnPositions (){
        int[][] positions = new int[teams.length][2];
        for (int i = 0; i < teams.length ; i++){
            positions[i] = Utility.convert1Dto2D(pawnPositions[i], board.getWidth());
        }
        return positions;
    }

    private List<int[]> getAdjPawnCoordsAndDirection(int[] targetPawnCoord, int[][] OtherPawnCoords) {
        List<int[]> adjPawnCoords = new ArrayList<>();
        for (int[] coord : OtherPawnCoords) {
            // Skip comparison with itself
            if (coord[0] == targetPawnCoord[0] && coord[1] == targetPawnCoord[1]) {
                continue;
            }

            // Check left
            if (targetPawnCoord[0] == coord[0] && targetPawnCoord[1] - 1 == coord[1]) {
                // coord is above targetCoord
                adjPawnCoords.add(new int[] {coord[0],  coord[1], 3});
            }
            // Check below
            if (targetPawnCoord[0] + 1 == coord[0] && targetPawnCoord[1] == coord[1]) {
//                return 1; // coord is to the right of targetCoord
                adjPawnCoords.add(new int[] {coord[0],  coord[1], 2});
            }
            // Check right
            if (targetPawnCoord[0] == coord[0] && targetPawnCoord[1] + 1 == coord[1]) {
               // coord is below targetCoord
                adjPawnCoords.add(new int[] {coord[0],  coord[1], 1});
            }
            // Check above
            if (targetPawnCoord[0] - 1 == coord[0] && targetPawnCoord[1] == coord[1]) {
                 // coord is to the left of targetCoord
                adjPawnCoords.add(new int[] {coord[0],  coord[1], 0});
            }
        }
        // No directly adjacent coordinate found
        if (adjPawnCoords.isEmpty()){
            return null;
        }

       return adjPawnCoords;
    }

    private boolean isDirectionBlockedByPawn (int dir, List<int[]> adjPawnCoords){
        if (adjPawnCoords == null){
            return false;
        }
        for (int[] coords : adjPawnCoords) {
            if (coords[2] == dir) {
               return true;
            }
        }
        return false;
    }



    private boolean checkIfPawnPathsBlocked (){
        int [][] winningLocations = findTeamWinLocations();

        for (int i = 0 ; i< teams.length; i++){
            int[] coords = Utility.convert1Dto2D(pawnPositions[i], board.getWidth());
            boolean checkRow = winningLocations[i][1] == 0;
            if(!board.canReachTarget(coords[0], coords[1], winningLocations[i][0], winningLocations[i][0], checkRow)){
                return false;
            };
        }

        return true;
    }

    private int[][] findTeamWinLocations(){
        //team order: bottom top left right

        //[team number] [row/col number , 0= row, 1=col]
        int[][] winningLocations = new int [teams.length][2];

        for (int i = 0 ; i< teams.length; i++){
            if ( i == 0){
                winningLocations[i][0] = 0; //row 0
                winningLocations[i][1] = 0;
            }
            else if (i == 1){
                winningLocations[i][0] = board.getHeight()-1; //last row
                winningLocations[i][1] = 0;
            }
            else if (i == 2){
                winningLocations[i][0] = board.getWidth()-1; //last col
                winningLocations[i][1] = 1;
            }
            else{
                winningLocations[i][0] = 0; //first col
                winningLocations[i][1] = 1;
            }

        }
        return winningLocations;
    }




    //TODO:
    //  stats

}
