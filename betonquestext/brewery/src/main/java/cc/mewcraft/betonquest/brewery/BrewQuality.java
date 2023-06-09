package cc.mewcraft.betonquest.brewery;

import org.betonquest.betonquest.exceptions.InstructionParseException;

public class BrewQuality {
    private final int low;
    private final int high;

    public BrewQuality(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public BrewQuality(String qualityString) throws InstructionParseException {
        String[] split = qualityString.split("-", 2);
        if (split.length > 2) {
            throw new InstructionParseException("The argument \"quality\" must either be in the form of X or X-Y");
        } else if (split.length == 2) {
            try {
                low = Integer.parseInt(split[0]);
                high = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new InstructionParseException("The argument \"quality\" can only contains integers");
            }
        } else {
            try {
                int quality = Integer.parseInt(qualityString);
                low = quality;
                high = quality;
            } catch (NumberFormatException e) {
                throw new InstructionParseException("The argument \"quality\" must be an integer");
            }
        }
    }

    public boolean contains(int number) {
        return (number >= low && number <= high);
    }
}
