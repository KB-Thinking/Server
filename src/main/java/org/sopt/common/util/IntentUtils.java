package org.sopt.common.util;

import java.text.Normalizer;

public final class IntentUtils {
    private IntentUtils() {
    }

    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC).trim();
        return n.toLowerCase();
    }

    public static boolean isMoveIntent(String text) {
        return includesAny(text,
                "이동해줘", "이동 시켜줘", "이동시켜줘", "바로 이동", "바로 가입", "바로 진행",
                "이동", "이동하자", "연결해줘", "화면으로 이동", "가입 페이지로 이동");
    }

    public static boolean includesAny(String text, String... keywords) {
        if (text == null || text.isEmpty()) return false;
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }
}
