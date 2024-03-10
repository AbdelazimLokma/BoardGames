/**
 * Piece Class used to represent generic pieces.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class Piece <T>{
    private T type;

    public Piece(T t){
        type = t;
    }

    public T getType() {
        return type;
    }

    public void setType(T type){
        this.type = type;
    }

    public String toString(){
        return type.toString();
    }

}
