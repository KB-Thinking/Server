package org.sopt.common.scenario;

import org.sopt.common.util.IntentUtils;
import org.sopt.common.type.Route;
import org.sopt.common.type.ScenarioStateStore;
import org.sopt.dto.ChatResponse;
import org.springframework.stereotype.Component;

@Component
public class FundScenario implements Scenario {
    private final ScenarioStateStore state;

    public FundScenario(ScenarioStateStore state) {
        this.state = state;
    }

    @Override
    public ChatResponse respond(Long userId, String text) {
        int step = state.getStep(userId);

        if (IntentUtils.includesAny(text, "수입", "자산", "관리", "고정비", "변동비")) step = Math.max(step, 0);
        if (IntentUtils.includesAny(text, "상품", "저축", "투자")) step = Math.max(step, 1);
        if (IntentUtils.includesAny(text, "연금저축", "적립식", "차이")) step = Math.max(step, 2);
        if (IntentUtils.includesAny(text, "시작", "어떻게", "가입")) step = Math.max(step, 3);
        if (IntentUtils.includesAny(text, "성향", "추천", "분석")) step = Math.max(step, 4);
        if (IntentUtils.includesAny(text, "밸런스", "다이나믹", "차이")) step = Math.max(step, 5);
        if (IntentUtils.includesAny(text, "선택", "가자", "선호", "밸런스 2040")) step = Math.max(step, 6);

        String reply;
        switch (step) {
            case 0 -> {
                reply = "수입 패턴을 보면 불규칙 요소가 있어요. 먼저 고정비/변동비를 구분하고, 남는 금액은 저축과 투자를 병행하는 방식을 권해요.";
                state.setStep(userId, 1);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 1 -> {
                reply = "선택지로 연금저축과 적립식 펀드가 있어요. 연금저축은 세액공제가 장점이고, 적립식 펀드는 매월 정기 투자로 자산 증식에 도움돼요. 어떤 쪽이 더 끌리세요?";
                state.setStep(userId, 2);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 2 -> {
                reply = "연금저축은 노후 준비와 세액공제에 유리하고, 적립식 펀드는 투자 성향에 맞춰 펀드를 선택해 정기 투자할 수 있어요.";
                state.setStep(userId, 3);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 3 -> {
                reply = "적립식 펀드는 KB 모바일 뱅킹 앱 ‘펀드’ 메뉴에서 시작할 수 있어요. 원하는 펀드를 고르고 금액을 설정하면 됩니다. 투자 성향 분석 후 맞춤 펀드를 추천드릴게요.";
                state.setStep(userId, 4);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 4 -> {
                reply = "분석 결과, 안정 추구 성향이라면 ‘KB밸런스 2040 펀드’ 또는 ‘KB다이나믹 주식형 펀드’를 추천드려요. 장기적인 관점에서 안정성을 고려한 조합입니다.";
                state.setStep(userId, 5);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 5 -> {
                reply = "‘KB밸런스 2040’은 자산배분으로 리스크를 분산해 안정적 성장을 지향해요. ‘KB다이나믹 주식형’은 수익 잠재력은 크지만 변동성이 있습니다. 어느 쪽이 더 맞으실까요?";
                state.setStep(userId, 6);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 6 -> {
                reply = "좋아요! 선택하신 펀드로 진행할게요. 지금 바로 가입 페이지로 이동해 드릴까요?";
                state.setStep(userId, 7);
                return ChatResponse.of(reply, Route.NONE);
            }
            default -> {
                reply = "준비가 되셨다면 ‘바로 이동’이라고 말씀해 주세요. 가입 페이지로 연결해 드릴게요.";
                state.setStep(userId, 7);
                return ChatResponse.of(reply, Route.NONE);
            }
        }
    }
}