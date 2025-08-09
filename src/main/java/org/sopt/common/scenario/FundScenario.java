package org.sopt.common.scenario;

import org.sopt.IntentDetector;
import org.sopt.IntentType;
import org.sopt.common.type.Route;
import org.sopt.dto.ChatResponse;
import org.springframework.stereotype.Component;

import static org.sopt.common.util.IntentUtils.includesAny;


@Component
public class FundScenario implements Scenario {

    @Override
    public ChatResponse respond(Long userId, String text) {
        IntentType intent = IntentDetector.detect(text);

        if (intent == IntentType.RESTART || intent == IntentType.NONE) {
            return new ChatResponse(
                    "수입이 불규칙할 땐 고정비/변동비를 먼저 구분하고, 남는 금액은 저축과 투자를 병행하는 방식을 권해요. " +
                            "원하시면 ‘어떤 상품이 있어?’라고 물어보세요.", Route.NONE.value());
        }

        if (intent == IntentType.PRODUCT) {
            String msg = "옵션으로는 ‘연금저축’과 ‘적립식 펀드’가 있어요. " +
                    "연금저축은 세액공제가 장점이고, 적립식 펀드는 매월 정기 투자로 자산 증식에 도움돼요. " +
                    "둘의 차이가 궁금하면 ‘차이점 알려줘’, 시작하려면 ‘어떻게 시작해?’라고 해보세요.";
            return new ChatResponse(msg, Route.NONE.value());
        }

        if (intent == IntentType.START) {
            String msg = "적립식 펀드는 KB 모바일 뱅킹 앱 ‘펀드’ 메뉴에서 시작할 수 있어요. 원하는 펀드를 선택하고 금액을 설정하면 됩니다. " +
                    "원하시면 성향에 맞는 펀드를 ‘추천’해 드릴게요.";
            return new ChatResponse(msg, Route.NONE.value());
        }

        if (intent == IntentType.PROFILE) {
            String msg = "안정 선호라면 ‘KB밸런스 2040 펀드’ 또는 ‘KB다이나믹 주식형 펀드’를 추천드려요. " +
                    "더 끌리는 쪽을 말씀해 주세요. (예: ‘밸런스 2040’)";
            return new ChatResponse(msg, Route.NONE.value());
        }

        if (intent == IntentType.CHOOSE || includesAny(text, "밸런스 2040")) {
            String msg = "좋습니다! ‘KB밸런스 2040 펀드’로 진행할게요. 지금 바로 가입 페이지로 이동해 드릴까요? " +
                    "‘바로 이동해줘’라고 하시면 연결해 드립니다.";
            return new ChatResponse(msg, Route.NONE.value());
        }

        if (intent == IntentType.MOVE) {
            return new ChatResponse("펀드 가입 페이지로 이동합니다. 투자 금액과 자동이체를 설정해 주세요!", Route.FUND.value());
        }

        return new ChatResponse("자산관리는 고정비/변동비 구분 → 남는 금액 저축+투자 권장. " +
                "‘상품’, ‘차이점’, ‘추천’, ‘어떻게 시작해?’ 중 하나를 말씀해 주세요.", Route.NONE.value());
    }
}