import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Manages console interactions for a game application, including welcoming users, game selection,
 * replay functionality, and difficulty settings.
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public class ConsoleController {

    public void welcomeMessage(){
        System.out.println("Hello there!, welcome to the game launcher!");
        System.out.println("");
        this.chooseGame();
    }

    public static void chooseGame(){

        List<String> gameTypes = GameFactory.gameTypes;

        displayOptions(gameTypes, "Please choose a game to play:");
        System.out.println("- Enter 'quit'/'q' to quit.");

        int choice = Input.getIntInput(1, gameTypes.size(), "main menu");
        if (choice != -1){
            System.out.println("You have chosen to play " + gameTypes.get(choice-1) + "!" );

            Game game = GameFactory.createGame(GameType.values()[choice-1]);

        }
        System.exit(0); //exit the program
    }

    public static  void playAgain(Object participants, Game g){
        boolean restart = false;
        boolean inputCon = true;

        while(inputCon){
            String response = Input.getStringInput(3, "Would you like to play another game? (yes/no)");
            if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")){
                inputCon = false;
                restart = true;
            }
            else if(response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")){
                if (g.getNumPlayers() == 1){ //game is a 1 player game, display player stats
                    Player p = (Player) participants;
                    inputCon = false;
                    System.out.println("Before you go, "+ p.getpName() + " you won " + p.getNumWins() + " games in this session, NICE!");
                    System.out.println("Closing " + g.getGameName() + " Game. Goodbye, "+ p.getpName() + "!");
                }
                else{ //game is multiplayer
                    Team[] teams = (Team[]) ((Object[]) participants);
                    int maxNumWins = 0;
                    int maxMovesMade = 0;
                    Team maxWinsId = teams[0];
                    Team maxMovesId = teams[0];

                    for(Team t: teams){
                        if (t.getTeamStats()[0] > maxNumWins){
                            maxNumWins = t.getTeamStats()[0];
                            maxWinsId = t;
                        }
                        if (t.getTeamStats()[1] > maxMovesMade){
                            maxMovesMade = t.getTeamStats()[1];
                            maxMovesId = t;
                        }
                    }
                    System.out.println("Before you go, lets have a look at some statistics:");
                    System.out.println("    -Team: " + maxWinsId.getTeamName() + " has won the most games with " + maxNumWins + " games won!");
                    System.out.println("    -Team: " + maxMovesId.getTeamName() + " was the most active team, with " + maxMovesMade + " moves made in all games played!");
                }
                inputCon = false;
            }
            else{
                System.out.println("Invalid input! Please enter 'yes' or 'no'");
            }
        }
        if (restart){
            g.reset();
            Difficulty difficulty;
            if(g.hasDifficulty){
                 difficulty = chooseDifficulty();
            }
            else{
                 difficulty = Difficulty.EASY;
            }

            g.createValidBoardState(difficulty);
            System.out.println("Here is your new " + g.getGameName() + " game: \n" );
            g.render();
            g.start();
        }
        else{
            ConsoleController.chooseGame();
        }

    }


    public static int[] inputBoardDim() {
        int width = 0, height = 0;

        width = Input.getIntInput(SlidingPuzzleGame.minSize, SlidingPuzzleGame.maxSize, "Enter the board width:");
        height = Input.getIntInput(SlidingPuzzleGame.minSize, SlidingPuzzleGame.maxSize, "Enter the board height:");

        return new int[]{width, height};
    }

    public static Player createPlayer(){
        Player player = null;
        String name = Input.getStringInput(12, "Please enter your player username:");
        player = new Player(name);
        return player;
    }

    public static Player createPlayer(int teamNum){
        Player player = null;
        String name = Input.getStringInput(12, "Please enter your player username:");
        player = new Player(name, teamNum);
        return player;
    }

    public static Difficulty chooseDifficulty(){
        String prompt = "What difficulty would you like the puzzle to be? \nPlease select type the number corresponding to the difficulties shown below:";


        displayOptions(Arrays.asList(Difficulty.values()), prompt);

        int choice = Input.getIntInput(1, Difficulty.values().length, "");

        System.out.println("You have chosen to play on the " + Difficulty.values()[choice-1].toString() + " difficulty!" );

        return  Difficulty.values()[choice-1];
    }

    public static <T> void displayOptions(List<T> options, String prompt){
        System.out.println(prompt);
        int counter = 1;
        for (T option: options){
            System.out.println("- Press " + counter + " for " + option.toString());
            counter++;
        }
    }

    public static Team[] createTeams(){
        int numTeams = Input.getIntInput(2,4, "Please input the number of teams that will be playing," +
                "\nremember that each team must have an equal number of players:");

        int numPlayersPerTeam = Input.getIntInput(1,4, "Please input the number of players per team:");

        Team[] toRet = new Team[numTeams];

        for (int i = 0; i< numTeams; i++){
            String teamName = Input.getStringInput(15, "Team " + (i+1) + ", please input your team name:");
            System.out.println("Great, lets get some info about players in "+ teamName);
            Team team = new Team(teamName, numPlayersPerTeam);
            for (int p = 0; p < numPlayersPerTeam; p++){
                System.out.println("Let's get player # "+ (p+1) + "'s name for this team: "+ teamName);
                team.getPlayers()[p] = ConsoleController.createPlayer(i);

            }
            toRet[i] = team;
        }
        return toRet;
    }

    public static void announceTeamScores(Team[] teams, Integer[] teamPoints) {
        List<Map.Entry<Team, Integer>> scoreBoard = new ArrayList<>();
        for (int i = 0; i < teams.length; i++) {
            scoreBoard.add(new AbstractMap.SimpleEntry<>(teams[i], teamPoints[i]));
        }

        scoreBoard.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Check for ties
        Integer previousScore = null;
        int position = 1; // Position in ranking

        for (int i = 0; i < scoreBoard.size(); i++) {
            Map.Entry<Team, Integer> entry = scoreBoard.get(i);

            // Determine if the current team is tied with the previous team
            if (previousScore != null && entry.getValue().equals(previousScore)) {
                // Tied situation
                System.out.println(String.format("Team %s is tied for place %d with %d points", entry.getKey().getTeamName(), position, entry.getValue()));
            } else {
                // Update position if not a tie or first entry
                position = i + 1;
                if (i == 0) { // First place
                    System.out.println(String.format("Team %s has won with %d points", entry.getKey().getTeamName(), entry.getValue()));
                    entry.getKey().incrementNumWins();
                } else { // Non-tied, non-first place
                    System.out.println(String.format("Team %s is in place %d with %d points", entry.getKey().getTeamName(), position, entry.getValue()));
                }
            }
            previousScore = entry.getValue(); // Update previousScore for next iteration comparison
        }
    }
    
}
