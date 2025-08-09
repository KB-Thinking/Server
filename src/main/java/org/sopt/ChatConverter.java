package org.sopt;


import org.sopt.common.scenario.FundScenario;
import org.sopt.common.scenario.LimitScenario;
import org.sopt.common.scenario.StockScenario;
import org.sopt.common.type.ScenarioType;
import org.sopt.common.util.IntentUtils;
import org.sopt.dto.ChatResponse;
import org.sopt.service.UserProfileService;
import org.springframework.stereotype.Service;


@Service
public class ChatConverter {
    private final UserProfileService profiles;
    private final LimitScenario limitScenario;
    private final FundScenario fundScenario;
    private final StockScenario stockScenario;

    public ChatConverter(UserProfileService profiles,
                         LimitScenario limitScenario,
                         FundScenario fundScenario,
                         StockScenario stockScenario) {
        this.profiles = profiles;
        this.limitScenario = limitScenario;
        this.fundScenario = fundScenario;
        this.stockScenario = stockScenario;
    }

    public ChatResponse handle(Long userId, String rawText) {
        String text = IntentUtils.normalize(rawText);
        ScenarioType scenario = profiles.resolveScenario(userId);

        return switch (scenario) {
            case LIMIT -> {
                var r = limitScenario.respond(userId, text);
                yield new ChatResponse(r.responseText(), r.route());
            }
            case FUND -> {
                var r = fundScenario.respond(userId, text);
                yield new ChatResponse(r.responseText(), r.route());
            }
            case STOCK -> {
                var r = stockScenario.respond(userId, text);
                yield new ChatResponse(r.responseText(), r.route());
            }
        };
    }
}