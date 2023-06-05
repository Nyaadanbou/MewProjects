package com.ranull.proxychatbridge.common.util;

import java.util.Map;

public final class StringUtil {
    public static String replaceAll(String string, Map<String, String> stringMap) {
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            string = string.replaceAll(entry.getKey(), entry.getValue());
        }

        return string;
    }

    public static String toTitleCase(String string) {
        if (string != null && !string.isEmpty()) {
            if (string.length() == 1) {
                return string.toUpperCase();
            } else {
                String[] split = string.split(" ");
                StringBuilder stringBuilder = new StringBuilder(string.length());

                for (String section : split) {
                    if (section.length() > 1) {
                        stringBuilder.append(section.substring(0, 1).toUpperCase())
                                .append(section.substring(1).toLowerCase());
                    } else {
                        stringBuilder.append(section.toUpperCase());
                    }

                    stringBuilder.append(" ");
                }

                return stringBuilder.toString().trim();
            }
        }

        return string;
    }
}
