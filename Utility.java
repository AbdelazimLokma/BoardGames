public class Utility {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";


    public static String colorString(String input, Integer colorCode) {
        String coloredString;

        switch (colorCode) {
            case 0:
                coloredString = ANSI_RED + input + ANSI_RESET;
                break;
            case 1:
                coloredString = ANSI_GREEN + input + ANSI_RESET;
                break;
            case 2:
                coloredString = ANSI_BLUE + input + ANSI_RESET;
                break;
            case 3:
                coloredString = ANSI_YELLOW + input + ANSI_RESET;
                break;
            default:
                coloredString = input;
        }

        return coloredString;
    }

    public static int findIndex(Integer[] arr, Integer t)
    {

        if (arr == null) {
            return -1;
        }

        int len = arr.length;
        int i = 0;

        while (i < len) {

            if (arr[i].equals(t)) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }


}
