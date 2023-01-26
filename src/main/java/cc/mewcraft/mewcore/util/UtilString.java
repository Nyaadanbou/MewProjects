package cc.mewcraft.mewcore.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class UtilString {

    public static String format(double money) {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
        double roundOff = Math.round(money * 100.0) / 100.0;
        return format.format(roundOff);
    }

}
