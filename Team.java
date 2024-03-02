/**
 * Represents a team in a game, encapsulating information about its players, name, number of wins, and moves made.
 * The team's size (number of players) is set upon creation. This class provides methods to access and modify team
 * properties, including the team's players, name, and to retrieve team statistics like wins and moves made.
 *
 * @author Abdelazim Lokma
 * @version 1.0
 * @since 2024-02-14
 */
public class Team {
    private Player[] players;
    private String teamName;

    private int numWins;
    private int movesMade;

    public Team(String teamName, int teamSize){
        players = new Player[teamSize];
        this.teamName = teamName;
        numWins = 0;
        movesMade = 0;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int[] getTeamStats(){
        return new int[] {numWins, movesMade};
    }

    public void incrementNumWins() {
        this.numWins++;
    }

    public void incrementMovesMade() {
        this.movesMade++;
    }
}
