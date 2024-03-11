import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extends the Board class to implement the specific logic and layout for any games with edges,
 * including the management of vertical and horizontal edges.
 * It provides functionalities to initialize the board edges, check if a box is completed,
 * render the board state, and determine if the entire board is filled. Methods are included to
 * get the edges around a box, identify undrawn edges, and assess if adjacent boxes are completed
 * upon drawing an edge.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */

public class BoardWithEdges extends Board{

    private Edge[][] verticalEdges;
    private Edge[][] horizontalEdges;


    public BoardWithEdges(int width, int height) {
        super(width, height);
        initializeEdges();
    }

    public void initializeEdges(){
        verticalEdges = new Edge[this.getHeight()][this.getWidth() + 1];
        horizontalEdges = new Edge[this.getHeight() + 1][this.getWidth()];

        // Initialize vertical edges
        for (int i = 0; i < this.getHeight(); i++) {
            for (int j = 0; j < this.getWidth() + 1; j++) {
                verticalEdges[i][j] = new Edge(false);
            }
        }

        // Initialize horizontal edges
        for (int i = 0; i < this.getHeight() + 1; i++) {
            for (int j = 0; j < this.getWidth(); j++) {
                horizontalEdges[i][j] = new Edge(false);
            }
        }
    }

    public Edge[] getBoxEdges(int row, int col) {
        Edge top = horizontalEdges[row][col];
        Edge bottom = horizontalEdges[row + 1][col];
        Edge left = verticalEdges[row][col];
        Edge right = verticalEdges[row][col + 1];
        return new Edge[] {top, right, bottom, left};
    }

    public boolean checkIfAdjacentBoxCompleted(int row, int col, Edge e){
        Edge[] edges = getBoxEdges(row, col);
        int edgeInd = 0;
        for (int i = 0; i < edges.length; i++){
            if (e.equals(edges[i])){
                edgeInd = i;
            }
        }

        if (edgeInd == 0 && row != 0){ //top edge
            return boxIsDrawn(row-1, col);
        } else if (edgeInd == 2 && row != this.getHeight()-1) { // bottom edge
            return boxIsDrawn(row+1, col);
        } else if (edgeInd == 1 && col != this.getWidth()-1) { // right edge
            return boxIsDrawn(row, col+1);
        } else if (edgeInd == 3 && col != 0) { //left edge
            return boxIsDrawn(row, col-1);
        }
        return false;

    }

    public List<Edge> getUndrawnBoxEdges(int row, int col, boolean setEdgeName) {
        List<Edge> toRet = new ArrayList<>();
        Edge[] edges = getBoxEdges(row, col);
        for (int i = 0; i < edges.length; i++ ){
            if (!edges[i].isDrawn){
                if(setEdgeName){
                    edges[i].setType(getEdgeName(i));
                }
                toRet.add(edges[i]);
            }
        }
        return toRet;
    }

    public List<String> getUndrawnBoxEdgeNames(int row, int col) {
        List<String> toRet = new ArrayList<>();
        String[] names = {"up", "right", "down", "left"};
        Edge[] edges = getBoxEdges(row, col);
        for(int i = 0; i < edges.length; i++){
            if(!edges[i].isDrawn){
                toRet.add(names[i]);
            }
        }
        return toRet;
    }

    public String getEdgeName(int i){
        String[] names = {"up", "right", "down", "left"};
        return names[i];
    }

    public int getIndexFromDirection (String direction){
        String[] names = {"up", "right", "down", "left"};
        for (int i = 0; i < names.length; i++){
            if (names[i].equals(direction)){
                return i;
            }
        }
        return -1;
    }


    public void renderRowHorizontalLine(int row) {
        for (int col = 0; col < super.getWidth(); col++){ //printing top line of each row
            System.out.print("+");

            if (row == super.getHeight() && getBoxEdges(row-1, col)[2].isDrawn){
                System.out.print("-----");
            }else if (  row != super.getHeight() && getBoxEdges(row, col)[0].isDrawn){
                System.out.print("-----");
            }
            else{
                System.out.print("     ");
            }
        }
        System.out.print("+");

    }

    public void renderRowVerticalLines(int row) {
        for(int i = 0; i < 1; i++){
            String line = "";
            for (int col = 0; col <  super.getWidth(); col++ ){
                if (getBoxEdges(row, col)[3].isDrawn){
                    String tileValue = tiles[row][col].getPiece().toString();

                    if (Integer.parseInt(tileValue) < 10){
                        line += "|  " + tileValue + "  ";
                    }
                    else{
                        line += "|  " + tileValue + " ";
                    }

                }
                else{
                    String tileValue = tiles[row][col].getPiece().toString();
                    if (Integer.parseInt(tileValue) < 10){
                        line += "   " + tileValue + "  ";
                    }
                    else{
                        line += "   " + tileValue + " ";
                    }
                }
                if (col == super.getWidth() - 1 && getBoxEdges(row, col)[1].isDrawn){
                    line += "|";
                }
            }
            System.out.println("\n"+line);
        }
    }

    public void renderRowVerticalLines(int row, Integer[] pawnPositions, List<Integer> validTiles) {
        for(int i = 0; i < 1; i++){
            String line = "";
            for (int col = 0; col <  super.getWidth(); col++ ){
                if (getBoxEdges(row, col)[3].isDrawn){
                    String tileValue = tiles[row][col].getPiece().toString();
                    String tileVal = tileValue;
                    if (Arrays.asList(pawnPositions).contains(Integer.parseInt(tileValue))){
                        tileValue = Utility.colorString(tileValue, Utility.findIndex(pawnPositions, Integer.parseInt(tileValue)));
                    } else if (validTiles!= null && validTiles.contains(Integer.parseInt(tileValue))) {
                        tileValue = Utility.colorString(tileValue, -1);
                    }

                    if (Integer.parseInt(tileVal) < 10){
                        line += "|  " + tileValue + "  ";
                    }
                    else{
                        line += "|  " + tileValue + " ";
                    }

                }
                else{
                    String tileValue = tiles[row][col].getPiece().toString();
                    String tileVal = tileValue;

                    if (Arrays.asList(pawnPositions).contains(Integer.parseInt(tileValue))){
                        tileValue = Utility.colorString( tileValue, Utility.findIndex(pawnPositions, Integer.parseInt(tileValue)) );
                    }
                    else if (validTiles!= null && validTiles.contains(Integer.parseInt(tileValue))) {
                        tileValue = Utility.colorString(tileValue, -1);
                    }

                    if (Integer.parseInt(tileVal) < 10){
                        line += "   " + tileValue + "  ";
                    }
                    else{
                        line += "   " + tileValue + " ";
                    }
                }
                if (col == super.getWidth() - 1 && getBoxEdges(row, col)[1].isDrawn){
                    line += "|";
                }
            }
            System.out.println("\n"+line);
        }
    }
    @Override
    public void renderBoard() {
        for(int row = 0;  row < super.getHeight(); row++){
            renderRowHorizontalLine(row);
            renderRowVerticalLines(row);
        }
        renderRowHorizontalLine(super.getHeight());


    }

    public void renderBoard(Integer[] pawnPositions,  List<Integer> validTiles) {
        for(int row = 0;  row < super.getHeight(); row++){
            renderRowHorizontalLine(row);
            renderRowVerticalLines(row, pawnPositions, validTiles);
        }
        renderRowHorizontalLine(super.getHeight());


    }

    public boolean boxIsDrawn(int row, int col){
        Edge[] edges = this.getBoxEdges(row, col);
        for(Edge e: edges){
            if (!e.isDrawn){
                return false;
            }
        }
        return true;
    }

    public boolean boardIsDrawn(){
        for (int row = 0; row < super.getHeight(); row++){
            for(int col = 0; col < super.getWidth(); col++){
                if (!boxIsDrawn(row,col)){
                    return false;
                }
            }
        }
        return true;
    }



    public boolean canReachTarget(int x, int y, int targetRow, int targetCol, boolean checkRow) {

        boolean[][] visited = new boolean[super.getHeight()][super.getWidth()];
        return dfs(x, y, targetRow, targetCol, visited, checkRow);
    }

    /**
     * implements the DFS algorithm to see whether a wall placement should be allowed or not
     * @param x
     * @param y
     * @param targetRow
     * @param targetCol
     * @param visited
     * @param checkRow
     * @return
     */
    private boolean dfs(int x, int y, int targetRow, int targetCol, boolean[][] visited, boolean checkRow) {
        if ((checkRow && x == targetRow) || (!checkRow && y == targetCol)) {
            return true;
        }
        if (x < 0 || x >= super.getHeight() || y < 0 || y >= super.getWidth() || visited[x][y]) {
            return false;
        }

        visited[x][y] = true;

        Edge[] boxEdges = getBoxEdges(x, y);

        // Up: Check if there is no horizontal edge above and no wall above
        if (x > 0 && !boxEdges[0].isDrawn && !visited[x-1][y] && dfs(x - 1, y, targetRow, targetCol, visited, checkRow) ) {
            return true;
        }

        // Down: Check if there is no horizontal edge below and no wall below
        if (x < super.getHeight() - 1  && !boxEdges[2].isDrawn && !visited[x+1][y] && dfs(x + 1, y, targetRow, targetCol, visited, checkRow)) {
            return true;
        }

        // Left: Check if there is no vertical edge to the left and no wall to the left
        if (y > 0 && !boxEdges[3].isDrawn && !visited[x][y-1] && dfs(x, y - 1, targetRow, targetCol, visited, checkRow)) {
            return true;
        }

        // Right: Check if there is no vertical edge to the right and no wall to the right
        if (y < super.getWidth() - 1  && !boxEdges[1].isDrawn && !visited[x][y+1] && dfs(x, y + 1, targetRow, targetCol, visited, checkRow)) {
            return true;
        }

        return false;
    }


    public void fillBorderWalls(){
        for (int y = 0; y < getWidth(); y++ ){
            for (int x = 0; x < getHeight(); x++ ){
                Edge[] edges = getBoxEdges(x,y);
                if (x == 0) {
                    edges[0].isDrawn = true;
                }
                else if(x == getHeight() - 1){
                    edges[2].isDrawn = true;
                }
                if (y == 0){
                    edges[3].isDrawn = true;
                }
                else if (y ==  getWidth()-1){
                    edges[1].isDrawn = true;
                }
            }
        }
    }




}
