/**
 * Represents a player in the game, including their name, symbol (e.g., for chess or tic-tac-toe), and win count.
 * Validates player names to ensure they are not purely numeric, supporting customization and tracking performance.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class Player {
    private String pName;
    private String pSymbol; //can represent both player color (black/white for chess) or actual symbol (X/O tic tac toe)

    private int teamNum;
    private int numWins;

    public Player(String name){
        this(name, null);
    }
    public Player(String name, String symbol) {
        this(name, symbol, -1);
    }

    public Player(String name, int teamNum) {
        this(name, null, teamNum);
    }

    public Player(String name, String symbol, int teamNum) {
        if (!isValidUserName(name)) {
            throw new IllegalArgumentException("Username must not be just a number.");
        }

        pName = name;
        pSymbol = symbol;
        numWins = 0;
        this.teamNum = teamNum;
    }


    private boolean isValidUserName(String username){
        //prevents a player from defining their name as just a number
        return (username.matches("\\D.*"));
    }

    public String getpName() {
        return pName;
    }

    public int getNumWins() {
        return numWins;
    }

    public int getTeamNum() {
        return teamNum;
    }

    public String getpSymbol() {
        return pSymbol;
    }

    public void setNumWins(int numWins) {
        this.numWins = numWins;
    }
}
