import java.util.List;
import java.util.Objects;
import java.util.Scanner;
/**
 * Handles user input from the console for various types of data, including integers and strings, with
 * validations for input range and length. It utilizes the {@code Scanner} class to read user input and
 * provides methods to ensure that this input meets specific criteria.
 *
 *
 * @author Abdelazim Lokma
 * @version 2.0
 * @since 2024-02-14
 */
public class Input {
    //handle integer input
    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput(int lowerBound, int upperBound, String prompt){

        if (prompt == "main menu"){

        }
        else{
            System.out.println(prompt);
        }
        int choice = 0;
        boolean cond = true;
        while (cond){
            if (scanner.hasNextInt()){
                choice = scanner.nextInt();
                if (choice < lowerBound || choice > upperBound) {
                    System.out.println("Invalid number. Please enter a number between " + lowerBound + " and " + upperBound + ".");
                }
                else{
                    cond = false;
                }
            }
            else if(inputIsQuit(scanner.next())){
                if (Objects.equals(prompt, "main menu")){
                    System.out.println("You've decided to terminate the program, exiting game launcher, goodbye!");
                    return -1;
                }
                else{
                    System.out.println("You've decided to quit, returning you to the game menu, goodbye!");
                    ConsoleController.chooseGame();
                }
            }
            else{
                System.out.println("Input is invalid. Please enter a number:");
            }
        }
        return choice;
    }

    public static int getIntInput(List<Integer> numSet, String prompt){

        if (prompt != "main menu"){
            System.out.println(prompt);
        }
        int choice = 0;
        boolean cond = true;
        while (cond){
            if (scanner.hasNextInt()){
                choice = scanner.nextInt();
                if (!numSet.contains(choice) ) {
                    System.out.println("Invalid number. Please enter a number from the following: " + numSet + ".");
                }
                else{
                    cond = false;
                }
            }
            else if(inputIsQuit(scanner.next())){
                if (Objects.equals(prompt, "main menu")){
                    System.out.println("You've decided to terminate the program, exiting game launcher, goodbye!");
                    return -1;
                }
                else{
                    System.out.println("You've decided to quit, returning you to the game menu, goodbye!");
                    ConsoleController.chooseGame();
                }
            }
            else{
                System.out.println("Input is invalid. Please enter a number:");
            }
        }
        return choice;
    }

    public static String getStringInput(int inputLen, String prompt){
        System.out.println(prompt);
        String input = "";
        boolean condition = false;
        while (!condition) {
            input = scanner.next();
            if (input.length() > inputLen){
                System.out.println("Error - please shorten your input to be less than " + inputLen + "characters long.");
            }
            else if (input.matches(".*[0-9].*")){
                System.out.println("Error - input cannot include a number, please retry.");
            }
            else{
                condition = true;
            }
        }
        return input;
    }

    private static boolean inputIsQuit(String input){
        if (input.toLowerCase().equals("quit") || input.toLowerCase().equals("q") || input.toLowerCase().equals("exit")){
            return true;
        }
        return false;
    }


}
