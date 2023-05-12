package cc.mewcraft.mewfishing.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This is the system that allows for a random range or exact numbers to be inputted in the config. Valid Inputs:
 * <pre>
 * {@code [4;10]} --> Picks a random number between 4 and 10.
 * {@code 10}     --> The number 10.
 * </pre>
 */
public final class NumberStylizer {

    /**
     * Allows for the input of a random range or an exact number. Valid Inputs:
     * <pre>
     * {@code [4;10]} --> Picks a random number between 4 and 10.
     * {@code 10} --> The number 10.
     * </pre>
     *
     * @param input the String input
     *
     * @return the number (if invalid 1 is returned)
     */
    public static int getStylizedInt(String input) {
        // [20;25]
        if (input.contains(";")) {
            String v = input.replace("[", "").replace("]", "");
            String[] out = v.split(";");
            try {
                int num1 = Integer.parseInt(out[0]);
                int num2 = Integer.parseInt(out[1]);
                return ThreadLocalRandom.current().nextInt(num1, num2 + 1);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                return 1;
            }
        }
        // if just num
        else {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                return 1;
            }
        }
    }

}
