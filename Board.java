import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Represents a game board composed of a grid of tiles. It supports initialization with a specified width and height,
 * retrieval of tiles by coordinates, finding adjacent tiles, and rendering the board state to the console. The board
 * is flexible, allowing for various game implementations that require a grid layout. Methods include adding tiles,
 * retrieving tile coordinates, rendering the board visually, and operations to support game mechanics like finding
 * adjacent tiles and flattening the board for streamlined processing.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public class Board {
    protected Tile[][] tiles;
    private int width;
    private int height;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
    public Board(int width, int height){
        this.width = width;
        this.height= height;
        tiles = new Tile[height][width];

        for (int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                tiles[row][col] = new Tile(null);
            }
        }
    }

    //Coordinates are 0-indexed
    public Tile getTile(int row, int col){
       return tiles[row][col];
    }

    public int[] getTileCoords(Tile t){
        for (int x = 0; x < height; x++){
            for(int y = 0; y < width; y++){
                if (tiles[x][y].equals(t)){
                    return new int[]{x, y};
                }
            }
        }
        throw new RuntimeException("Tile does not exist on board");
    }

    public void renderBoard() {
        // Print the top border
        for (int x = 0; x < width; x++) {
            System.out.print("+---");
        }
        System.out.println("+");

        for (int y = 0; y < height; y++) {
            // Print the tile values along with vertical dividers
            for (int x = 0; x < width; x++) {
                System.out.print("|");
                String tileValue = tiles[y][x].getPiece().toString();
                // Adjust the tile value to ensure it takes up two spaces for alignment
                System.out.print(String.format("%2s ", tileValue));
            }
            System.out.println("|"); // Close the grid line

            // Print a separating line between each row, except after the last row
            if (y < height - 1) {
                for (int x = 0; x < width; x++) {
                    System.out.print("|---");
                }
                System.out.println("|");
            }
        }

        // Print the bottom border
        for (int x = 0; x < width; x++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    public List<Tile> findAdjacentTiles(Tile tile){
        List<Tile> adjacentTiles = new ArrayList<Tile>(); //neighbors added in clockwise order
        int[] tileCoords = this.getTileCoords(tile);

        //check top neighbor:
        if (tileCoords[0] != 0){ //if r != 0, add top neighbor
            adjacentTiles.add(this.getTile(tileCoords[0]-1, tileCoords[1]));
        }
        //check right neighbor:
        if (tileCoords[1] != this.width-1){ //if c != width-1, add right neighbor
            adjacentTiles.add(this.getTile(tileCoords[0], tileCoords[1]+1));
        }
        //check bottom neighbor:
        if (tileCoords[0] != this.height-1){ //if r != height-1, add bottom neighbor
            adjacentTiles.add(this.getTile(tileCoords[0]+1, tileCoords[1]));
        }
        //check left neighbor:
        if (tileCoords[1] != 0){ //if c != 0, add left neighbor
            adjacentTiles.add( this.getTile(tileCoords[0], tileCoords[1]-1));
        }

        return adjacentTiles;
    }

    public Tile[] flattenBoard(){
        return Arrays.stream(this.tiles)
                .flatMap(Arrays::stream)
                .toArray(Tile[]::new);
    }

    protected void numberBoardTiles(){
        for (int x = 0; x < this.getHeight(); x++){
            for (int y = 0; y < this.getWidth(); y++){
                Piece<Integer> p = new Piece<Integer>(x* this.getWidth()+y+1);
                this.getTile(x,y).setPiece(p);
            }
        }
    }

}
