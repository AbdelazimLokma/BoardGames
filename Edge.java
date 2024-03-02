/**
 * Represents an edge in the Dots and Boxes game, extending the Piece class with a String type.
 * It includes a boolean state to indicate whether the edge has been drawn.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class Edge extends Piece<String>{

    public boolean isDrawn;
    public Edge(boolean isDrawn) {
        super("");
        this.isDrawn = isDrawn;
    }


}
