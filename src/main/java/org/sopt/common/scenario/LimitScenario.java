package org.sopt.common.scenario;

import org.sopt.common.util.IntentUtils;
import org.sopt.common.type.Route;
import org.sopt.common.type.ScenarioStateStore;
import org.sopt.dto.ChatResponse;
import org.springframework.stereotype.Component;

@Component
public class LimitScenario implements Scenario {
    private final ScenarioStateStore state;

    public LimitScenario(ScenarioStateStore state) {
        this.state = state;
    }

    @Override
    public ChatResponse respond(Long userId, String text) {
        int step = state.getStep(userId);

        if (IntentUtils.includesAny(text, "출금", "현금", "300만", "한도")) step = Math.max(step, 0);
        if (IntentUtils.includesAny(text, "건강보험", "고지서", "서류", "제출")) step = Math.max(step, 1);
        if (IntentUtils.includesAny(text, "증권", "송금", "옮기")) step = Math.max(step, 2);

        String reply;
        switch (step) {
            case 0 -> {
                reply = "현재 출금 한도는 100만원까지 설정되어 있어요. 한도를 넘기려면 신분증 인증과 추가 서류(예: 건강보험 고지서, 세금 고지서) 제출이 필요할 수 있어요.";
                state.setStep(userId, 1);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 1 -> {
                reply = "네, 건강보험 고지서 제출이 가능해요. KB 모바일 뱅킹 앱의 ‘서류 제출’ 메뉴에서 사진 촬영 후 업로드하시면 됩니다. 접수 후 한도 상향 절차가 진행돼요.";
                state.setStep(userId, 2);
                return ChatResponse.of(reply, Route.NONE);
            }
            case 2 -> {
                if (IntentUtils.includesAny(text, "증권", "옮기", "송금")) {
                    reply = "맞아요. 증권 계좌로 송금할 때도 한도 초과가 필요하면 서류 제출 후 상향 절차가 필요해요. 지금 바로 서류 제출 화면으로 안내해 드릴까요?";
                } else {
                    reply = "한도 상향이 필요하시면 지금 바로 서류 제출 화면으로 안내해 드릴까요?";
                }
                state.setStep(userId, 3);
                return ChatResponse.of(reply, Route.NONE);
            }
            default -> {
                reply = "준비가 되셨다면 ‘이동해줘’라고 말씀해 주세요. 서류 제출 화면으로 연결해 드릴게요.";
                state.setStep(userId, 3);
                return ChatResponse.of(reply, Route.NONE);
            }
        }
    }
}
