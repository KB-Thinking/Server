package org.sopt.common.scenario;

import com.fasterxml.jackson.databind.JsonNode;
import org.sopt.IntentDetector;
import org.sopt.IntentType;
import org.sopt.common.type.Route;
import org.sopt.common.util.IntentUtils;
import org.sopt.dto.ChatResponse;
import org.sopt.service.UserProfileService;
import org.springframework.stereotype.Component;

@Component
public class LimitScenario implements Scenario {
    private final UserProfileService profiles;

    public LimitScenario(UserProfileService profiles) {
        this.profiles = profiles;
    }

    @Override
    public ChatResponse respond(Long userId, String text) {
        JsonNode root = profiles.readUserJson(userId);
        int dailyLimit = root != null ? root.path("limits").path("withdrawalDaily").asInt(1_000_000) : 1_000_000;

        IntentType intent = IntentDetector.detect(text);
        boolean hasSecurities = IntentUtils.includesAny(
                text, "증권", "증권으로", "송금", "송금하려", "이체",
                "옮기", "옮기려", "옮기려고", "돈 옮기", "돈 옮기려", "돈 옮기려고", "돈옮기",
                "보내", "보내려"
        );
        if (hasSecurities) intent = IntentType.SECURITIES;
        if (intent == IntentType.NONE || intent == IntentType.RESTART || intent == IntentType.WITHDRAW) {
            String msg = "현재 출금 한도는 " + "1,000,000" + "원까지 설정되어 있어요. "
                    + "한도를 넘기려면 신분증 인증과 추가 서류(예: 건강보험 고지서, 세금 고지서) 제출이 필요할 수 있어요.";
            return ChatResponse.of(msg, Route.NONE);
        }

        if (intent == IntentType.DOCS) {
            String msg = "네, 건강보험 고지서 등 서류 제출이 가능해요. KB 모바일 뱅킹 앱의 ‘서류 제출’ 메뉴에서 사진 촬영 후 업로드하시면 됩니다. "
                    + "접수 후 한도 상향 절차가 진행돼요.";
            return ChatResponse.of(msg, Route.NONE);
        }

        if (intent == IntentType.SECURITIES) {
            String msg = "맞아요. 증권 계좌로 송금할 때도 한도 초과 시 서류 제출 후 상향 절차가 필요해요. "
                    + "지금 바로 서류 제출 화면으로 안내해 드릴까요?";
            return ChatResponse.of(msg, Route.NONE);
        }

        if (intent == IntentType.MOVE) {
            return ChatResponse.of("서류 제출 화면으로 이동합니다. 서류를 준비해 주세요!", Route.LIMIT);
        }

        String fallback = "요청을 이해했어요. 요약하면, 출금 한도는 " + "1,000,000"
                + "원이고, 한도 상향 시 서류 제출이 필요합니다. ‘이동해줘’라고 하면 바로 연결해 드릴게요.";
        return ChatResponse.of(fallback, Route.NONE);
    }
}