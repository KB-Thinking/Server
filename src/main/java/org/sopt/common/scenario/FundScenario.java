package org.sopt.common.scenario;

import org.sopt.IntentDetector;
import org.sopt.IntentType;
import org.sopt.common.type.Route;
import org.sopt.dto.ChatResponse;
import org.springframework.stereotype.Component;


@Component
public class FundScenario implements Scenario {

    @Override
    public ChatResponse respond(Long userId, String text) {
        IntentType intent = IntentDetector.detect(text);

        if (intent == IntentType.RESTART || intent == IntentType.NONE) {
            return new ChatResponse(
                    "수영님의 수입을 분석한 결과, 불규칙한 패턴의 수입을 확인했어요. 이 경우에는 고정비와 변동비를 명확하게 구분하는 것이 중요해요. " +
                            "그에 맞는 저축 계획을 세우시면 더 효율적으로 자산을 관리할 수 있어요. 예를 들어, 매달 일정 금액을 저축하고, 남은 금액을 유동적으로 관리하는 방법이 있습니다.",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.PRODUCT) {
            return new ChatResponse(
                    "수영님은 일정하지 않은 수입을 고려해 안정적인 재정 관리를 원하시는군요. 고정비를 먼저 분리한 후, 나머지 여유 자금을 저축과 함께 투자로 활용하실 수 있어요. " +
                            "예를 들어, KB의 '연금저축'이나 '적립식 펀드'와 같은 상품을 추천드려요. 장기적으로 안정적인 수익을 기대할 수 있는 옵션입니다.",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.DIFF_PRODUCT) {
            return new ChatResponse(
                    "연금저축은 세액 공제 혜택을 제공하고, 장기적인 노후 준비에 적합해요. " +
                            "반면, 적립식 펀드는 투자 성향에 맞는 펀드를 선택해 매월 정기적으로 투자할 수 있어서, 자산 증식에 도움이 됩니다. " +
                            "어느 쪽이 더 관심이 가시나요?",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.START) {
            return new ChatResponse(
                    "적립식 펀드는 KB 모바일 뱅킹 앱에서 바로 시작할 수 있어요. '펀드' 메뉴에서 원하는 펀드를 선택하고, 투자 금액을 설정한 뒤 투자 시작하면 됩니다. " +
                            "또한, 고객님의 투자 성향에 맞춘 맞춤형 펀드를 추천드릴 수 있어요.",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.PROFILE || intent == IntentType.RECOMMEND) {
            return new ChatResponse(
                    "수영님, 귀하의 투자 성향을 분석한 결과, 안정적인 수익을 선호하시기 때문에 'KB밸런스 2040 펀드'나 'KB다이나믹 주식형 펀드'와 같은 상품을 추천드립니다. " +
                            "이 두 펀드는 장기적인 안정성을 고려한 펀드입니다. 이 펀드들 중에서 선택하실 수 있어요.",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.DIFF_FUND) {
            return new ChatResponse(
                    "'KB밸런스 2040 펀드'는 안정적인 자산 배분을 통해 리스크를 분산시키며, 장기적인 성장에 초점을 맞춘 펀드입니다. " +
                            "반면, 'KB다이나믹 주식형 펀드'는 더 높은 수익률을 추구하지만, 변동성이 있을 수 있는 주식형 펀드입니다. " +
                            "안전성과 수익성 중 어떤 것을 더 중시하시나요?",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.CHOOSE) {
            return new ChatResponse(
                    "좋습니다! 'KB밸런스 2040 펀드'는 장기적인 안정적인 수익을 추구하는 고객님에게 딱 맞는 펀드입니다. " +
                            "KB 모바일 뱅킹 앱에서 바로 가입하실 수 있어요. 지금 바로 펀드 가입 페이지로 이동할까요?",
                    Route.NONE.value()
            );
        }

        if (intent == IntentType.MOVE || intent == IntentType.CONFIRM) {
            return new ChatResponse(
                    "알겠습니다. 지금 'KB밸런스 2040 펀드' 가입 페이지로 이동합니다. 잠시만 기다려 주세요!",
                    Route.FUND.value()
            );
        }

        return new ChatResponse(
                "고정비/변동비를 구분한 뒤, 연금저축이나 적립식 펀드처럼 장기 옵션을 함께 검토하는 걸 권해요. " +
                        "‘상품’, ‘차이’, ‘추천’, ‘시작 방법’ 중 원하시는 흐름을 말씀해 주세요.",
                Route.NONE.value()
        );
    }
}