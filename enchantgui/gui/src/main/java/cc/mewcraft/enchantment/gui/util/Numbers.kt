package cc.mewcraft.enchantment.gui.util;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.TreeMap;

public class NumberUtil {

    private static final DecimalFormat FORMAT_ROUND_HUMAN;
    private static final TreeMap<Integer, String> ROMAN_MAP = new TreeMap<>();

    static {
        FORMAT_ROUND_HUMAN = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.ENGLISH));

        ROMAN_MAP.put(1000, "M");
        ROMAN_MAP.put(900, "CM");
        ROMAN_MAP.put(500, "D");
        ROMAN_MAP.put(400, "CD");
        ROMAN_MAP.put(100, "C");
        ROMAN_MAP.put(90, "XC");
        ROMAN_MAP.put(50, "L");
        ROMAN_MAP.put(40, "XL");
        ROMAN_MAP.put(10, "X");
        ROMAN_MAP.put(9, "IX");
        ROMAN_MAP.put(5, "V");
        ROMAN_MAP.put(4, "IV");
        ROMAN_MAP.put(1, "I");
    }

    public static @NotNull String format(double value) {
        return FORMAT_ROUND_HUMAN.format(value);
    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static @NotNull String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);

        int key = ROMAN_MAP.floorKey(number);
        if (number == key) {
            return ROMAN_MAP.get(number);
        }
        return ROMAN_MAP.get(key) + toRoman(number - key);
    }

    public static int[] splitIntoParts(int whole, int parts) {
        int[] arr = new int[parts];
        int remain = whole;
        int partsLeft = parts;
        for (int i = 0; partsLeft > 0; i++) {
            int size = (remain + partsLeft - 1) / partsLeft; // rounded up, aka ceiling
            arr[i] = size;
            remain -= size;
            partsLeft--;
        }
        return arr;
    }
}
