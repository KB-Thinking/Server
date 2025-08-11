package org.sopt;


public enum IntentType {
    NONE,
    RESTART,     // 리셋
    MOVE,        // 화면 이동
    CONFIRM,     // 긍정

    // LIMIT
    WITHDRAW,    // 출금/한도
    DOCS,        // 서류/제출/건보
    SECURITIES,  // 증권/송금/이체

    // FUND
    PRODUCT,     // 상품/저축/투자
    DIFF,        // 연금저축 vs 적립식 차이
    START,       // 시작/가입
    PROFILE,     // 성향/추천/분석
    CHOOSE,      // 특정 펀드 선택
    DIFF_PRODUCT,
    DIFF_FUND,

    // STOCK
    BRIEFING,    // 오늘 동향/브리핑/레포트
    TREND,       // 시장 추세/재분석
    RECOMMEND,   // 종목 추천
    ALERT        // 알림 설정/가격 알림
}