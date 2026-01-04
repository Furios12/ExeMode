package org.Exestudios.exeMode.utils;

public class TimeUtil {
    public static long parseDurationMillis(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.toLowerCase().trim();
        long multiplier = 0;
        try {
            if (s.endsWith("ms")) {
                multiplier = 1L;
                s = s.substring(0, s.length() - 2);
            } else if (s.endsWith("s")) {
                multiplier = 1000L;
                s = s.substring(0, s.length() - 1);
            } else if (s.endsWith("m")) {
                multiplier = 60_000L;
                s = s.substring(0, s.length() - 1);
            } else if (s.endsWith("h")) {
                multiplier = 3_600_000L;
                s = s.substring(0, s.length() - 1);
            } else if (s.endsWith("d")) {
                multiplier = 86_400_000L;
                s = s.substring(0, s.length() - 1);
            } else {
                // default seconds
                multiplier = 1000L;
            }
            long val = Long.parseLong(s);
            return val * multiplier;
        } catch (Exception e) {
            return 0;
        }
    }
}