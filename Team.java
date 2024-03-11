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

    private int stat1;
    private int stat2;

    private String statName1;

    private String statName2;

    public Team(String teamName, int teamSize){
        players = new Player[teamSize];
        this.teamName = teamName;
        stat1 = 0;
        stat2 = 0;
        statName1 = "";
        statName2 = "";
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
        return new int[] {stat1, stat2};
    }

    public String[] getTeamStatNames(){
        return new String[]{statName1, statName2};
    }

    public void setStatNames(String statName1, String statName2) {
        this.statName1 = statName1;
        this.statName2 = statName2;

    }

    public void incrementStat1() {
        this.stat1++;
    }

    public void incrementStat2() {
        this.stat2++;
    }
}
