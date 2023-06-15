package cc.mewcraft.townybonus.util;

public final class UtilNumber {

    public static int parseIntRangeLower(String amount) {
        final String[] split = amount.split("~");
        return Integer.parseInt(split[0]);
    }

    public static int parseIntRangeHigher(String amount) {
        final String[] split = amount.split("~");
        return Integer.parseInt(split[1]);
    }

}
