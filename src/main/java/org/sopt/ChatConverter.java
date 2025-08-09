package org.sopt;


import org.sopt.common.scenario.FundScenario;
import org.sopt.common.scenario.LimitScenario;
import org.sopt.common.scenario.StockScenario;
import org.sopt.common.type.Route;
import org.sopt.common.type.ScenarioStateStore;
import org.sopt.common.type.ScenarioType;
import org.sopt.common.util.IntentUtils;
import org.sopt.dto.ChatResponse;
import org.sopt.service.UserProfileService;
import org.springframework.stereotype.Service;

@Service
public class ChatConverter {
    private final UserProfileService profiles;
    private final ScenarioStateStore state;
    private final LimitScenario limitScenario;
    private final FundScenario fundScenario;
    private final StockScenario stockScenario;

    public ChatConverter(UserProfileService profiles,
                         ScenarioStateStore state,
                         LimitScenario limitScenario,
                         FundScenario fundScenario,
                         StockScenario stockScenario) {
        this.profiles = profiles;
        this.state = state;
        this.limitScenario = limitScenario;
        this.fundScenario = fundScenario;
        this.stockScenario = stockScenario;
    }

    public ChatResponse handle(Long userId, String rawText) {
        String text = IntentUtils.normalize(rawText);
        ScenarioType scenario = profiles.resolveScenario(userId);

        if (IntentUtils.isMoveIntent(text) && scenario != ScenarioType.STOCK) {
            Route route = (scenario == ScenarioType.LIMIT) ? Route.LIMIT : Route.FUND;
            state.reset(userId);
            String msg = (scenario == ScenarioType.LIMIT)
                    ? "네, 서류 제출 화면으로 이동합니다. 서류를 준비해 주세요!"
                    : "좋습니다. 선택하신 펀드 가입 페이지로 이동합니다!";
            return ChatResponse.of(msg, route);
        }
        return switch (scenario) {
            case LIMIT -> limitScenario.respond(userId, text);
            case FUND -> fundScenario.respond(userId, text);
            case STOCK -> stockScenario.respond(userId, text);
        };
    }
}
