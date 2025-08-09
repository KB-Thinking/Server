package org.sopt.common.util;

import java.text.Normalizer;


public final class IntentUtils {
    private IntentUtils() {
    }

    public static String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFKC).trim().toLowerCase();
    }

    public static boolean includesAny(String text, String... keywords) {
        if (text == null || text.isEmpty()) return false;
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }
}