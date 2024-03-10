import java.util.*;

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
        start();
    }

    @Override
    void initialize() {
        System.out.println("Welcome to Quoridor! The goal of this game is to get to your opponent's side\n" +
                "before your opponent gets to your side!");

        this.teams = ConsoleController.createTeams();

        this.numPlayers = teams[0].getPlayers().length * teams.length;

        this.teamPawns = new ArrayList<>();

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
        Random firstStart = new Random();
        int randomInt = firstStart.nextInt(teams.length);

        String[] currTeamInfo = ConsoleController.getTeamInfo(randomInt, teams);
        System.out.println("Team: " + currTeamInfo[1] + " (displayed on the board with the color: " + Utility.colorString(currTeamInfo[0], randomInt) +  ")\nhas won the draw, they get to go first!");

        Queue<Player> queue = ConsoleController.loadPlayerQueue(teams, randomInt);


        while (!checkWinCondition()){
            //draw player from queue, take input
            Player p = queue.poll();

            System.out.println("Player " + p.getpName() + ", of team: " +Utility.colorString(teams[p.getTeamNum()].getTeamName(), p.getTeamNum()) +  ", it is your turn!");

            int playerChoice = movePawnOrPlaceWall(p);

            updateGameState(playerChoice, p);

            render();
            //add to back of queue
            queue.add(p);
        }



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
    void updateGameState(int num){};
    void updateGameState(int playerChoice, Player p) {
        if(playerChoice == 1){ //player chooses to move pawn
            //TODO: pawn movement mechanic and player pawn movement choice mechanic
            movePawn(p);
        }
        else{
            placeWall(p);
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

    private int movePawnOrPlaceWall (Player p){
        List<String> options = new ArrayList<>();
        options.add("Move Pawn");
        if (wallsPerTeam[p.getTeamNum()] > 0){
            options.add("Add Wall");
            ConsoleController.displayOptions(options, "Player " + p.getpName() + ", would you like to move your pawn or place a wall");
            return Input.getIntInput(1, 2, "Please input the number corresponding to the desired option:");
        }
        else{
             System.out.println("Player " + p.getpName() + ", your team is out of walls, your only option is to move the pawn.");
             return 1;
        }

    }

    private void placeWall(Player p){
        int tileNum1D = Input.getIntInput(1, board.flattenBoard().length, "Please select which tile to add a wall to:");
        int[] tileCoord = Utility.convert1Dto2D(tileNum1D, board.getWidth());
        int[] edgeValidity = getValidEdges(tileCoord[0], tileCoord[1]);
        List<String> validEdges = new ArrayList<>();

        for (int i = 0 ; i < 4; i++){
            if (edgeValidity[i] != 0){
                validEdges.add(board.getEdgeName(i));
            }
        }


        ConsoleController.displayOptions(validEdges,"Now, please select which side of the tile to place an edge on:");
        int edgeIndex = Input.getIntInput(1, validEdges.size(), "Choose a number: ");
            edgeIndex--;

        if (edgeValidity[edgeIndex] == 1){
            String usrResp = Input.getStringInput(3,"You can only place a wall s.t it also covers its left neighbor, would you like to do this?");
            if ( usrResp.equals("YES") ||  usrResp.equals("yes") || usrResp.equals("y")){
                //TODO: check pawn DFS before placement
                board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
                placeAdjacentEdge(tileCoord, edgeIndex, 1);
            }
            //TODO: handle no case
        }
        else if (edgeValidity[edgeIndex] == 2){
            String usrResp = Input.getStringInput(3,"You can only place a wall s.t it also covers its right neighbor, would you like to do this?");
            if ( usrResp.equals("YES") ||  usrResp.equals("yes") || usrResp.equals("y")){
                //TODO: check pawn DFS before placement
                board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
                placeAdjacentEdge(tileCoord, edgeIndex, 0);
            }
            //TODO: handle no case
        }
        else{
            List<String> directions= new ArrayList<>();
            directions.add("left");
            directions.add("right");

            ConsoleController.displayOptions(directions,"Which direction do you want your wall to face?");
            int direction = Input.getIntInput(1, 2, "Choose a number: ");
            //TODO: check pawn DFS before placement
            board.getBoxEdges(tileCoord[0],tileCoord[1])[edgeIndex].isDrawn = true;
            placeAdjacentEdge(tileCoord, edgeIndex, direction);

        }

        System.out.println("Wall has been placed!");
        wallsPerTeam[p.getTeamNum()] --;
    }

    private void placeAdjacentEdge( int[] curBoxCoord, int edgeIndx, int direction){
        if (edgeIndx == 0){
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]-1)[0].isDrawn = true;
            }
            else{
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[0].isDrawn = true;
            }
        }
        else if (edgeIndx == 1){
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1])[1].isDrawn = true;
            }
            else{
                board.getBoxEdges(curBoxCoord[0]+1, curBoxCoord[1])[1].isDrawn = true;
            }
        }
        else if (edgeIndx == 2){
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1]-1)[2].isDrawn = true;
            }
            else{
                board.getBoxEdges(curBoxCoord[0], curBoxCoord[1]+1)[2].isDrawn = true;
            }
        }
        else{
            if (direction == 1){ //place to the left
                board.getBoxEdges(curBoxCoord[0]+1, curBoxCoord[1])[3].isDrawn = true;
            }
            else{
                board.getBoxEdges(curBoxCoord[0]-1, curBoxCoord[1])[3].isDrawn = true;
            }
        }
    }

    private int[] getValidEdges(int x, int y){ //TODO: too much repeated code, function can be shortened w/ 2 helpers

        List<Edge> undrawnEdges =  board.getUndrawnBoxEdges(x,y);
        int[] validEdges =  new int[4];

        for (int i = 0; i< undrawnEdges.size(); i++){
            if (i == 0){ //check top edges
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
            else if (i == 1){
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
            else if (i == 2){ //check bottom edges
                if (y == 0){
                    if (!board.getBoxEdges(x,y+1)[2].isDrawn){
                        validEdges[2] = 2;
                    }
                } else if (y == board.getWidth()-1) {
                    if (!board.getBoxEdges(x,y-1)[2].isDrawn){
                        validEdges[2] = 1;
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
                        validEdges[3] = 1;
                    }
                    else if (!board.getBoxEdges(x+1,y)[3].isDrawn) {
                        validEdges[3] = 2;
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
    }

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






    //Moves:
        //make edges behind walls/beyond board limit  (cases 2 & 3) //TODO: Abdel
        //pawn must be able to DFS to destination row / col
        //pawn can jump over other pawn to reach tile (case 6)
            //function to get tiles adj to a pawn
        //
}
