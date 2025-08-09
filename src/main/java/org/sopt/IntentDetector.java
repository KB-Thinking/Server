package org.sopt;

import org.sopt.common.util.IntentUtils;

import java.util.regex.Pattern;


public final class IntentDetector {
    private IntentDetector() {}

    private static final Pattern MANWON_PATTERN =
            Pattern.compile("\\b(\\d{2,3})\\s*만\\s*원?\\b");

    public static IntentType detect(String raw) {
        if (raw == null || raw.isBlank()) return IntentType.NONE;
        String text = raw.toLowerCase().trim();

        if (IntentUtils.includesAny(text, "처음부터","다시 시작","리셋","reset","초기화"))
            return IntentType.RESTART;

        if (IntentUtils.includesAny(text, "이동해줘","이동 시켜줘","이동시켜줘","바로 이동","연결해줘","화면으로 이동","가입 페이지로 이동"))
            return IntentType.MOVE;

        if (IntentUtils.includesAny(text,
                "증권","증권으로","송금","송금하려","이체",
                "옮기","옮기려","옮기려고","돈 옮기","돈 옮기려","돈 옮기려고","돈옮기",
                "보내","보내려"))
            return IntentType.SECURITIES;

        if (IntentUtils.includesAny(text, "출금","현금","인출","한도","한도 풀어","한도 올려")
                || MANWON_PATTERN.matcher(text).find()
                || IntentUtils.includesAny(text, "300만","300만원"))
            return IntentType.WITHDRAW;

        if (IntentUtils.includesAny(text, "서류","제출","건강보험","건보","세금 고지서","어떻게 제출"))
            return IntentType.DOCS;

        boolean mentionsPension = IntentUtils.includesAny(text, "연금저축");
        boolean mentionsSip = IntentUtils.includesAny(text, "적립식");
        boolean asksDiff = IntentUtils.includesAny(text, "차이","비교","다르","무엇이 다르","뭐가 달라","어떤 차이");
        if (asksDiff || (mentionsPension && mentionsSip)) return IntentType.DIFF;

        if (IntentUtils.includesAny(text, "상품")
                || (IntentUtils.includesAny(text, "저축","투자") && IntentUtils.includesAny(text, "어떤","무슨","추천","있어","뭐가")))
            return IntentType.PRODUCT;

        if (IntentUtils.includesAny(text, "시작","가입","어떻게 시작","어떻게 가입","가입하려면"))
            return IntentType.START;

        if (IntentUtils.includesAny(text, "성향","맞춤","추천","분석"))
            return IntentType.PROFILE;

        if (IntentUtils.includesAny(text, "밸런스 2040","kb밸런스","다이나믹 주식형","kb 다이나믹","다이나믹"))
            return IntentType.CHOOSE;

        if (IntentUtils.includesAny(text,
                "추세","시장 추세","요새 시장","요즘 시장","최근 시장",
                "시장 분위기","시장 상황","재분석","다시 분석","다시 분석해줘","추세를 통해","추세 통해","추세 기반",
                "분위기를 통해","시장 흐름으로","흐름으로 다시"))
            return IntentType.TREND;

        if (IntentUtils.includesAny(text,
                "동향","브리핑","레포트","간단하게","간단한",
                "분석해서 설명","분석해서","분석해줘","설명해줘","설명해 줄래","오늘"))
            return IntentType.BRIEFING;

        if (IntentUtils.includesAny(text, "추천","추가 투자","추가적인 투자","포트폴리오 외","다른 종목","nvidia","엔비디아"))
            return IntentType.RECOMMEND;

        if (IntentUtils.includesAny(text, "알림 설정","알람 설정","알림을 설정","알림 맞춰","가격 알림","알림 등록","알림 추가","알림 켜",
                "nvidia 알림","엔비디아 알림","알림","알람"))
            return IntentType.ALERT;

        if (IntentUtils.includesAny(text, "좋아","네","응","그래","진행","해주세요","해줘"))
            return IntentType.CONFIRM;

        return IntentType.NONE;
    }
}