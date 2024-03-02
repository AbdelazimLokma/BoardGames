/**
 * Encapsulates a tile on a game board, holding a piece and providing methods to get and set the piece.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class Tile {

    private Piece piece;

    public Tile(Piece p){
        piece = p;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece p){
        this.piece = p;
    }


}
