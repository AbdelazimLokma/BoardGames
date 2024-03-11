/**
 * Utility class providing various helper methods for manipulating strings, arrays, and coordinates.
 * These methods include coloring strings, finding the index of an element in an array, and converting
 * between 2D and 1D coordinates.
 */
public class Utility {

    /**
     * ANSI color codes for formatting strings in different colors.
     */
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_MAGENTA = "\u001B[35m";

    /**
     * Colorizes the input string based on the given color code.
     *
     * input: The string to be colorized.
     * colorCode: The color code to be applied. Use 0 for red, 1 for green, 2 for blue, 3 for yellow,
     *                  -1 for magenta, and any other value to keep the original color.
     * returns the colorized string.
     */
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
            case -1:
                coloredString = ANSI_MAGENTA + input + ANSI_RESET;
                break;
            default:
                coloredString = input;
        }

        return coloredString;
    }

    /**
     * Finds the index of an element in the specified array.
     *
     * arr: The array to search within.
     * t: The element to find.
     * returns the index of the element if found, or -1 if the element is not present in the array.
     */
    public static int findIndex(Integer[] arr, Integer t) {
        if (arr == null) {
            return -1;
        }

        int len = arr.length;
        int i = 0;

        while (i < len) {
            if (arr[i].equals(t)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * Converts 2D coordinates to 1D index.
     *
     * row: The row coordinate.
     * column: The column coordinate.
     * width:  The width of the 2D grid.
     * returns the corresponding 1D index.
     */
    public static int convert2Dto1D(int row, int column, int width) {
        return (row * width) + column + 1;
    }

    /**
     * Converts 1D index to 2D coordinates.
     *
     * index: The 1D index.
     * width: The width of the 2D grid.
     * returns an array containing the row and column coordinates.
     */
    public static int[] convert1Dto2D(int index, int width) {
        int zeroBasedIndex = index - 1;

        int row = zeroBasedIndex / width;
        int column = zeroBasedIndex % width;

        return new int[]{row, column};
    }
}
